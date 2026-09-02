package it.cardgames.briscola.controller;

import java.util.Optional;

import it.cardgames.briscola.engine.BriscolaRuleEngine;
import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.BriscolaDeck;
import it.cardgames.briscola.model.Player;
import it.cardgames.briscola.model.ItalianSuit;

public class BriscolaMatch {

    private MatchState state = MatchState.NOT_STARTED;
	private final Player player1;
	private final Player player2;
	private final BriscolaDeck deck;
	
	private BriscolaCard groundBriscola;
	private boolean groundBriscolaAvailable;
	private int leaderIndex;

	
	public BriscolaMatch(Player player1, Player player2, BriscolaDeck deck) {
		if (player1 == null || player2 == null || deck == null) {
            throw new IllegalArgumentException("Giocatori e mazzo non possono essere null.");
        }
		
        this.player1 = player1;
        this.player2 = player2;
        this.deck = deck;
	}


    public void startMatch() {
        if (state == MatchState.IN_PROGRESS) {
            throw new IllegalStateException("La partita è già in corso.");
        }

        deck.reset();
        deck.shuffle();

        player1.reset();
        player2.reset();

        for (int i = 0; i < 3; i++) {
            deck.draw().ifPresent(player1::receiveCard);
            deck.draw().ifPresent(player2::receiveCard);
        }

        groundBriscola = deck.draw()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Mazzo insufficiente per estrarre la briscola."
                        ));

        groundBriscolaAvailable = true;
        leaderIndex = 0;

        state = MatchState.IN_PROGRESS;
    }

	public BriscolaCard getGroundBriscola() {
        return groundBriscola;
    }

    public ItalianSuit getBriscolaSuit() {
        if (groundBriscola == null) {
            throw new IllegalStateException("Partita non ancora iniziata.");
        }
        return groundBriscola.suit();
    }

    public int getRemainingCardsInDeck() {
        return deck.remainingCards() + (groundBriscolaAvailable ? 1 : 0);
    }
	
    public boolean isGameOver() {
        return state == MatchState.FINISHED;
    }

    public RoundResult playRound() {
        if (state != MatchState.IN_PROGRESS) {
            throw new IllegalStateException(
                    "La partita non è in corso."
            );
        }

        Player leadPlayer =
                (leaderIndex == 0) ? player1 : player2;

        Player followPlayer =
                (leaderIndex == 0) ? player2 : player1;

        BriscolaCard leadCard =
                leadPlayer.playCard(null, getBriscolaSuit());

        BriscolaCard followCard =
                followPlayer.playCard(
                        leadCard,
                        getBriscolaSuit()
                );

        int result = BriscolaRuleEngine.evalWinning(
                leadCard,
                followCard,
                getBriscolaSuit()
        );

        Player roundWinner =
                (result == 0) ? leadPlayer : followPlayer;

        roundWinner.collectCards(leadCard, followCard);

        leaderIndex =
                (roundWinner == player1) ? 0 : 1;

        drawCards(roundWinner);

        if (player1.getHand().isEmpty() &&
                player2.getHand().isEmpty()) {
            state = MatchState.FINISHED;
        }

        return new RoundResult(
                leadPlayer,
                leadCard,
                followPlayer,
                followCard,
                roundWinner,
                BriscolaRuleEngine.getPoints(leadCard)
                        + BriscolaRuleEngine.getPoints(followCard)
        );
    }

	private void drawCards(Player roundWinner) {
		Player roundLoser = (roundWinner == player1) ? player2 : player1;

        // Pescata del vincitore
        Optional<BriscolaCard> firstDraw = deck.draw();
        if (firstDraw.isPresent()) {
            roundWinner.receiveCard(firstDraw.get());

            // Pescata del perdente
            Optional<BriscolaCard> secondDraw = deck.draw();
            if (secondDraw.isPresent()) {
                roundLoser.receiveCard(secondDraw.get());
                
            } else if (groundBriscolaAvailable) {
                roundLoser.receiveCard(groundBriscola);
                groundBriscolaAvailable = false;
            }
        } else if (groundBriscolaAvailable) {
            roundWinner.receiveCard(groundBriscola);
            groundBriscolaAvailable = false;
        }		
	}
	
	
	/**
     * @return il giocatore vincitore, o null in caso di pareggio (60 a 60)
     */
    public Player getWinner() {
        if (state != MatchState.FINISHED) {
            throw new IllegalStateException(
                    "La partita non è ancora terminata."
            );
        }

        if (player1.getScore() > player2.getScore()) {
            return player1;
        }

        if (player2.getScore() > player1.getScore()) {
            return player2;
        }

        return null;
    }

}
