package it.cardgames.briscola.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BriscolaDeck {
	
	private List<BriscolaCard> cards = new ArrayList<BriscolaCard>(40); 
	
	public BriscolaDeck() {
		reset();
	}

	private void reset() {
		getCards().clear();
		for (Suit suit : Suit.values()) {
			for (int i = 1; i <= 10; i++) {
				getCards().add(new BriscolaCard(i, suit));
			}
		}
	}
	
	
	public Optional<BriscolaCard> draw() {
		if (cards.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(cards.removeFirst());
	}
	
	
	public void shuffle() {
		Collections.shuffle(cards);
	}

	protected List<BriscolaCard> getCards() {
		return cards;
	}

	public int remainingCards() {
		return cards.size();
	}
	
	

}
