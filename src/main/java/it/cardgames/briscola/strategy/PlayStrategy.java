package it.cardgames.briscola.strategy;

import java.util.List;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.Suit;

@FunctionalInterface
public interface PlayStrategy {

	/**
	 * @param hand the player's hand
	 * @param tableCard card on the table
	 * @param briscolaSuit the briscola suit
	 * @return the index of the card chosen
	 */
	int chooseCard(List<BriscolaCard> hand, BriscolaCard tableCard, Suit briscolaSuit);
	
}
