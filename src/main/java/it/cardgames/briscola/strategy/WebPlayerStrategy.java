package it.cardgames.briscola.strategy;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.ItalianSuit;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class WebPlayerStrategy implements PlayStrategy {

    private CompletableFuture<Integer> pendingMove;
    private Consumer<BriscolaCard> onTurnRequest;
    private volatile List<BriscolaCard> currentHand;

    public void setOnTurnRequest(Consumer<BriscolaCard> onTurnRequest) {
        this.onTurnRequest = onTurnRequest;
    }

    @Override
    public int chooseCard(List<BriscolaCard> hand, BriscolaCard tableCard, ItalianSuit briscolaSuit) {
        this.currentHand = hand;
        this.pendingMove = new CompletableFuture<>();

        if (onTurnRequest != null) {
            onTurnRequest.accept(tableCard);
        }

        try {
            return pendingMove.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Attesa mossa interrotta o client disconnesso", e);
        } finally {
            this.currentHand = null;
        }
    }

    public synchronized boolean submitMove(int cardIndex) {
        if (pendingMove != null && !pendingMove.isDone() && currentHand != null) {
            if (cardIndex >= 0 && cardIndex < currentHand.size()) {
                pendingMove.complete(cardIndex);
                return true;
            }
        }
        return false;
    }

    public synchronized void abortMatch(String reason) {
        if (pendingMove != null && !pendingMove.isDone()) {
            pendingMove.completeExceptionally(new IllegalStateException(reason));
        }
    }
}