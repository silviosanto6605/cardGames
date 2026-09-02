package it.cardgames.briscola.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BriscolaDeckTest {

	
	@Test
	void testBriscolaDeck() {
		BriscolaDeck briscolaDeck = new BriscolaDeck();
		BriscolaDeck briscolaDeck2 = new BriscolaDeck();
		
		
		briscolaDeck.getCards().removeFirst();
		
		assertThat(briscolaDeck.getCards().size())
			.isEqualTo(39);
		
		briscolaDeck = new BriscolaDeck();
		assertThat(briscolaDeck.getCards())
			.containsExactlyElementsOf(briscolaDeck2.getCards());
		
	}
	
	@Test
	void testBriscolaDeckDraw() {
		BriscolaDeck briscolaDeck = new BriscolaDeck();
		
		BriscolaCard expected = briscolaDeck.getCards().getFirst();
		
		
		assertThat(briscolaDeck.draw())
			.isPresent()
			.contains(expected);
		
		assertThat(briscolaDeck.getCards())
			.hasSize(39);
		
		assertThat(briscolaDeck.getCards())
			.doesNotContain(expected);
		
		
	}
	@Test
	void testBriscolaDeckDrawEmpty() {
		BriscolaDeck deck = new BriscolaDeck();
		while (deck.remainingCards() > 0) {
            deck.draw();
        }

        assertThat(deck.remainingCards()).isZero();
        assertThat(deck.draw()).isEmpty();
		
		
	}

}
