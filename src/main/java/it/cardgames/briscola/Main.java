package it.cardgames.briscola;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import it.cardgames.briscola.controller.BriscolaMatch;
import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.BriscolaDeck;
import it.cardgames.briscola.model.Player;
import it.cardgames.briscola.net.ClientMessage;
import it.cardgames.briscola.net.ServerStateDto;
import it.cardgames.briscola.strategy.WebPlayerStrategy;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;

public class Main {

    private static final Queue<WsContext> waitingPlayers = new ConcurrentLinkedQueue<>();
    private static final Map<String, WebPlayerStrategy> playerStrategies = new ConcurrentHashMap<>();
    private static final Map<String, WsContext> opponentMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
            });
        });

        app.ws("/game", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("Nuova connessione: " + ctx.sessionId());
                waitingPlayers.add(ctx);
                tryStartMatch();
            });

            ws.onMessage(ctx -> {
                ClientMessage msg = ctx.messageAsClass(ClientMessage.class);
                if ("PLAY".equals(msg.action())) {
                    WebPlayerStrategy strategy = playerStrategies.get(ctx.sessionId());
                    if (strategy != null) {
                        strategy.submitMove(msg.cardIndex());
                    }
                }
            });

            ws.onClose(ctx -> {
                System.out.println("Disconnesso: " + ctx.sessionId());
                waitingPlayers.remove(ctx);

                WebPlayerStrategy strategy = playerStrategies.remove(ctx.sessionId());
                if (strategy != null) {
                    strategy.abortMatch("Il giocatore si è disconnesso.");
                }

                WsContext oppCtx = opponentMap.remove(ctx.sessionId());
                if (oppCtx != null && oppCtx.session.isOpen()) {
                    opponentMap.remove(oppCtx.sessionId());
                    playerStrategies.remove(oppCtx.sessionId());
                    sendDisconnectNotice(oppCtx, "L'avversario si è disconnesso. Partita terminata.");
                }
            });

        });

        app.start(8080);
        System.out.println("Server Web avviato su http://localhost:8080");
    }

    private static synchronized void tryStartMatch() {
        if (waitingPlayers.size() >= 2) {
            WsContext p1Ctx = waitingPlayers.poll();
            WsContext p2Ctx = waitingPlayers.poll();

            WebPlayerStrategy strat1 = new WebPlayerStrategy();
            WebPlayerStrategy strat2 = new WebPlayerStrategy();

            playerStrategies.put(p1Ctx.sessionId(), strat1);
            playerStrategies.put(p2Ctx.sessionId(), strat2);

            Player player1 = new Player("Giocatore 1", strat1);
            Player player2 = new Player("Giocatore 2", strat2);

            p1Ctx.attribute("player", player1);
            p1Ctx.attribute("opponent", player2);
            p2Ctx.attribute("player", player2);
            p2Ctx.attribute("opponent", player1);

            BriscolaMatch match = new BriscolaMatch(player1, player2, new BriscolaDeck());

            strat1.setOnTurnRequest(tableCard -> {
                sendState(p1Ctx, match, true, tableCard, null, null, "È il tuo turno!");
                sendState(p2Ctx, match, false, tableCard, null, null, "In attesa dell'avversario...");
            });

            strat2.setOnTurnRequest(tableCard -> {
                sendState(p2Ctx, match, true, tableCard, null, null, "È il tuo turno!");
                sendState(p1Ctx, match, false, tableCard, null, null, "In attesa dell'avversario...");
            });

            System.out.println("Partita avviata tra " + p1Ctx.sessionId() + " e " + p2Ctx.sessionId());
            Thread.ofVirtual().start(() -> gameLoop(match, p1Ctx, p2Ctx, player1, player2));
        }
    }

    private static void gameLoop(BriscolaMatch match, WsContext p1, WsContext p2, Player player1, Player player2) {
        try {
            match.startMatch();

            while (!match.isGameOver()) {
                var roundResult = match.playRound();

                String resMsg = "Presa di " + roundResult.winner().getName() + " (+" + roundResult.pointsWon() + " pt)";

                sendRoundResult(p1, match, roundResult.leadCard(), roundResult.followCard(), roundResult.winner() == player1, resMsg);
                sendRoundResult(p2, match, roundResult.leadCard(), roundResult.followCard(), roundResult.winner() == player2, resMsg);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (match.isGameOver()) {
                Player winner = match.getWinner();
                String endMsg = (winner != null) ? "Vincitore: " + winner.getName() + "!" : "Pareggio!";
                sendState(p1, match, false, null, null, null, "Partita finita! " + endMsg);
                sendState(p2, match, false, null, null, null, "Partita finita! " + endMsg);
            }
        } catch (Exception e) {
            System.err.println("Partita interrotta anomala: " + e.getMessage());
        } finally {
            playerStrategies.remove(p1.sessionId());
            playerStrategies.remove(p2.sessionId());
            opponentMap.remove(p1.sessionId());
            opponentMap.remove(p2.sessionId());
        }
    }

    private static void sendState(WsContext ctx, BriscolaMatch match, boolean isTurn, BriscolaCard firstCard, BriscolaCard secondCard, Boolean wonByMe, String msg) {
        if (ctx.session.isOpen()) {
            Player me = ctx.attribute("player");
            Player opponent = ctx.attribute("opponent");

            ServerStateDto dto = new ServerStateDto(
                    "UPDATE",
                    me.getHand(),
                    firstCard,
                    secondCard,
                    match.getGroundBriscola(),
                    match.getRemainingCardsInDeck(),
                    me.getScore(),
                    opponent.getScore(),
                    isTurn,
                    wonByMe,
                    msg
            );
            ctx.send(dto);
        }
    }

    private static void sendDisconnectNotice(WsContext ctx, String msg) {
        if (ctx.session.isOpen()) {
            Player me = ctx.attribute("player");
            ServerStateDto dto = new ServerStateDto(
                    "DISCONNECT",
                    me != null ? me.getHand() : java.util.List.<BriscolaCard>of(),
                    null,
                    null,
                    null,
                    0,
                    me != null ? me.getScore() : 0,
                    0,
                    false,
                    null,
                    msg
            );
            ctx.send(dto);
        }
    }

    private static void sendRoundResult(WsContext ctx, BriscolaMatch match, BriscolaCard firstCard, BriscolaCard secondCard, boolean wonByMe, String msg) {
        if (ctx.session.isOpen()) {
            Player me = ctx.attribute("player");
            Player opponent = ctx.attribute("opponent");

            ServerStateDto dto = new ServerStateDto(
                    "ROUND_OVER",
                    me.getHand(),
                    firstCard,
                    secondCard,
                    match.getGroundBriscola(),
                    match.getRemainingCardsInDeck(),
                    me.getScore(),
                    opponent.getScore(),
                    false,
                    wonByMe,
                    msg
            );
            ctx.send(dto);
        }
    }

}