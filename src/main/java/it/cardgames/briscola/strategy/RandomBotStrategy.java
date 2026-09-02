package it.cardgames.briscola.strategy;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.ItalianSuit;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBotStrategy implements PlayStrategy {

    @Override
    public int chooseCard(List<BriscolaCard> hand, BriscolaCard tableCard, ItalianSuit briscolaSuit) {
        return ThreadLocalRandom
        		.current()
        		.nextInt(hand.size());
    }
}
