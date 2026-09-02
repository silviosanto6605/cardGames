package it.cardgames.briscola.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class BriscolaCardTest {

	@Test
	void testBriscolaCardConstructorValid() {
		BriscolaCard card = new BriscolaCard(10, Suit.SPADE);
		assertThat(card.suit())
			.isEqualTo(Suit.SPADE);
		assertThat(card.rank())
			.isEqualTo(10);
	}
	
	@Test
	void testBriscolaCardInvalidRank() {
		assertThatThrownBy(
				() -> {new BriscolaCard(-1, Suit.SPADE);}
				).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Il valore della carta deve essere tra 1 e 10. Ricevuto: -1");
	}
	
	@Test
	void testBriscolaCardConstructorNoSuit() {
		assertThatThrownBy(
				() -> {new BriscolaCard(10,null);}
				).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Il seme non può essere null.");
	
	}

	@Test
	void testGetRankName() {
		BriscolaCard card = new BriscolaCard(10, Suit.SPADE);
		assertThat(card.getRankName())
			.isEqualTo("Re");

	}	
	@Test
	void testToString() {
		BriscolaCard card = new BriscolaCard(10, Suit.SPADE);
		assertThat(card.toString())
			.isEqualTo("Re di SPADE");

	}	

}
