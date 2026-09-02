package it.cardgames.briscola.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.ItalianSuit;

class BriscolaRuleEngineTest {


	private final BriscolaCard card1 = new BriscolaCard(10, ItalianSuit.SPADE); // 4pt, potenza 8
	private final BriscolaCard card2 = new BriscolaCard(9, ItalianSuit.BASTONI); // 3pt, potenza 7
	private final BriscolaCard card3 = new BriscolaCard(8, ItalianSuit.BASTONI); // 2pt, potenza 6
	private final BriscolaCard card4 = new BriscolaCard(1, ItalianSuit.DENARI); // 11pt, potenza 10
	private final BriscolaCard card5 = new BriscolaCard(3, ItalianSuit.COPPE); // 10pt, potenza 9
	private final BriscolaCard card6 = new BriscolaCard(2, ItalianSuit.COPPE); // 0pt, potenza 1


	@Test
	void testGetPointsNullArgs() {
		assertThatThrownBy(
				()->{BriscolaRuleEngine.getPoints(null);}
				)
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("La carta non può essere null!");
	}
	
	@Test
	void testGetPoints() {

		assertThat(BriscolaRuleEngine.getPoints(card1)).isEqualTo(4);

		assertThat(BriscolaRuleEngine.getPoints(card2)).isEqualTo(3);

		assertThat(BriscolaRuleEngine.getPoints(card3)).isEqualTo(2);

		assertThat(BriscolaRuleEngine.getPoints(card4)).isEqualTo(11);

		assertThat(BriscolaRuleEngine.getPoints(card5)).isEqualTo(10);

		assertThat(BriscolaRuleEngine.getPoints(card6)).isEqualTo(0);

	}

	@Test
	void testGetPower() {
		assertThat(BriscolaRuleEngine.getPower(card1)).isEqualTo(8);

		assertThat(BriscolaRuleEngine.getPower(card2)).isEqualTo(7);

		assertThat(BriscolaRuleEngine.getPower(card3)).isEqualTo(6);

		assertThat(BriscolaRuleEngine.getPower(card4)).isEqualTo(10);

		assertThat(BriscolaRuleEngine.getPower(card5)).isEqualTo(9);

		assertThat(BriscolaRuleEngine.getPower(card6)).isEqualTo(1);
	}

	@Test
	void testGetPowerNullArgs() {
		assertThatThrownBy(
				()->{BriscolaRuleEngine.getPower(null);}
				)
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("La carta non può essere null!");
	}

	@Test
	void testEvalWinningNullArgs() {
		assertThatThrownBy(() -> {
			BriscolaRuleEngine.evalWinning(null, null, null);
		}).isInstanceOf(IllegalArgumentException.class).hasMessage("Carte o semi null non ammessi!");
	}

	@Test
	void testEvalWinningSameSuitNotBriscolaHighestWins() {

		// stesso seme , non di briscola -> vince + alta
		assertThat(
				BriscolaRuleEngine.evalWinning(
						new BriscolaCard(5, ItalianSuit.BASTONI),
						new BriscolaCard(7, ItalianSuit.BASTONI),
						ItalianSuit.COPPE)
				).isEqualTo(1);

		assertThat(
				BriscolaRuleEngine.evalWinning(
						new BriscolaCard(7, ItalianSuit.BASTONI),
						new BriscolaCard(5, ItalianSuit.BASTONI),
						ItalianSuit.COPPE)
				).isEqualTo(0);
	}

	@Test
	void testEvalWinningSameSuitBriscolaHighestWins() {
		// stesso seme, di briscola -> vince + alta
		assertThat(
				BriscolaRuleEngine.evalWinning(
						new BriscolaCard(3, ItalianSuit.BASTONI),
						new BriscolaCard(5, ItalianSuit.BASTONI),
						ItalianSuit.BASTONI)
				).isEqualTo(0);
	}

	@Test
	void testEvalWinningBriscolaVsNonBriscolaHighestWins() {

		// seme briscola vs non briscola -> vince briscola
		assertThat(
				BriscolaRuleEngine.evalWinning(
						new BriscolaCard(5, ItalianSuit.BASTONI),
						new BriscolaCard(7, ItalianSuit.COPPE),
						ItalianSuit.BASTONI)
				).isEqualTo(0);

		assertThat(
				BriscolaRuleEngine.evalWinning(
						new BriscolaCard(7, ItalianSuit.COPPE),
						new BriscolaCard(5, ItalianSuit.BASTONI),
						ItalianSuit.BASTONI)
				).isEqualTo(1);

	}

	@Test
	void testEvalWinningNoBriscolaDifferentSuitsFirstWins() {

		// semi diversi, non briscola -> vince primo
		assertThat(
				BriscolaRuleEngine.evalWinning(
						new BriscolaCard(5, ItalianSuit.BASTONI),
						new BriscolaCard(7, ItalianSuit.COPPE),
						ItalianSuit.DENARI)
				).isEqualTo(0);

	}

}
