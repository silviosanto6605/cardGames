package it.cardgames.briscola.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.cardgames.briscola.model.BriscolaDeck;
import it.cardgames.briscola.model.Player;
import it.cardgames.briscola.strategy.PlayStrategy;

class BriscolaMatchTest {
	
	private Player p1;
    private Player p2;
    private BriscolaDeck deck;
    private BriscolaMatch match;

	@BeforeEach
	void setUp() {
		PlayStrategy firstCardStrategy = (hand, tableCard, briscola) -> 0;
        p1 = new Player("Giocatore 1", firstCardStrategy);
        p2 = new Player("Giocatore 2", firstCardStrategy);
        deck = new BriscolaDeck();
        match = new BriscolaMatch(p1, p2, deck);
	}

	@Test
	public void testGetBriscolaSuit() {
		
		assertThatThrownBy(()-> match.getBriscolaSuit())
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Partita non ancora iniziata.");
		
		match.startMatch();
		assertThat(match.getBriscolaSuit())
			.isEqualTo(match.getGroundBriscola().suit());
	}
	
	
	@Test
	void testStartMatch() {
		match.startMatch();

        assertThat(p1.getHand()).hasSize(3);
        assertThat(p2.getHand()).hasSize(3);
        assertThat(match.getGroundBriscola()).isNotNull();
        
        // 40 carte totali - 3 (p1) - 3 (p2) = 34 carte rimanenti tra tallone e briscola a terra
        assertThat(match.getRemainingCardsInDeck()).isEqualTo(34);
        assertThat(match.isGameOver()).isFalse();	
    }

    @Test
    void newMatchIsNotStarted() {
        assertThat(match.isGameOver()).isFalse();
    }

    @Test
    void cannotPlayRoundBeforeStart() {
        assertThatThrownBy(() -> match.playRound())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testCannotRestartFinishedMatch() {
        match.startMatch();
        while (!match.isGameOver()) {
            match.playRound();
        }

        assertThatThrownBy(match::startMatch)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La partita è terminata. Crea una nuova partita per rigiocare.");
    }

    @Test
    void matchIsInProgressAfterStart() {
        match.startMatch();

        assertThat(match.isGameOver()).isFalse();
    }
	
	
	@Test
	void testCompleteMatch() {
		match.startMatch();

        int roundCounter = 0;
        while (!match.isGameOver()) {
            match.playRound();
            roundCounter++;
            assertThat(roundCounter).isLessThanOrEqualTo(20); 
        }

        assertThat(roundCounter).isEqualTo(20);
        assertThat(p1.getHand()).isEmpty();
        assertThat(p2.getHand()).isEmpty();
        assertThat(match.getRemainingCardsInDeck()).isZero();

        assertThat(p1.getScore() + p2.getScore()).isEqualTo(120);

        if (p1.getScore() != p2.getScore()) {
            assertThat(match.getWinner()).isNotNull();
        } else {
            assertThat(match.getWinner()).isNull();
        }
	}
	
	@Test
	void testPlayRoundAfterMatchFinishedThrowException() {
		match.startMatch();
        while (!match.isGameOver()) {
            match.playRound();
        }

        assertThatThrownBy(match::playRound)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("La partita non è in corso.");
    
	}
	@Test
	void testStartMatchWhileRunningThrowException() {
		match.startMatch();

        assertThatThrownBy(match::startMatch)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("La partita è già in corso.");

	}



}
