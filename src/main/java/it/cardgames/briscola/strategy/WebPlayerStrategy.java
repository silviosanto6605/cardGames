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

    public void setOnTurnRequest(Consumer<BriscolaCard> onTurnRequest) {
        this.onTurnRequest = onTurnRequest;
    }

    @Override
    public int chooseCard(List<BriscolaCard> hand, BriscolaCard tableCard, ItalianSuit briscolaSuit) {
        if (onTurnRequest != null) {
            onTurnRequest.accept(tableCard);
        }

        pendingMove = new CompletableFuture<>();
        try {
            return pendingMove.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Attesa mossa interrotta", e);
        }
    }

    public void submitMove(int cardIndex){
        if (pendingMove != null && !pendingMove.isDone()){
            pendingMove.complete(cardIndex);
        }
    }
}