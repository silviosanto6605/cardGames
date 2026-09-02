package it.cardgames.briscola.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.cardgames.briscola.strategy.PlayStrategy;

class PlayerTest {

	private Player player;
	
	private final BriscolaCard assoDenari = new BriscolaCard(1, Suit.DENARI);  // 11 pt
    private final BriscolaCard treCoppe = new BriscolaCard(3, Suit.COPPE);      // 10 pt
    private final BriscolaCard dueBastoni = new BriscolaCard(2, Suit.BASTONI);  // 0 pt
	
	@BeforeEach
	void setUp() throws Exception {
		//dummy strategy: estrae sempre la prima
		player = new Player("Silvio", (a,b,c)->0);
	}

	@Test
	void testPlayerWithArgs() {
		
		PlayStrategy dummyStrategy = (a,b,c)->0;
		Player player2 = new Player("John",dummyStrategy);
		
		assertThat(player2.getName())
			.isEqualTo("John");
		
		assertThat(player2.getStrategy())
			.isEqualTo(dummyStrategy);
		
		assertThat(player2.getHand())
			.isEmpty();
		
		assertThat(player2.getCollectedCards())
			.isEmpty();

	}
	@Test
	void testPlayerWithNullArgs() {
		
		assertThatThrownBy(
				()-> {new Player("John",null);})
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Strategia di gioco non può essere null!");
		
		assertThatThrownBy(
				()-> {new Player(" ",(a,b,c)->0);})
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Nome non può essere null o vuoto!");
	
		assertThatThrownBy(
				()-> {new Player(null,(a,b,c)->0);})
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Nome non può essere null o vuoto!");
		
		
	}

	@Test
	void testReceiveCard() {
		player.receiveCard(assoDenari);
        player.receiveCard(treCoppe);

        assertThat(player.getHand())
        	.hasSize(2)
        	.containsExactly(assoDenari, treCoppe);	
        
	}

	@Test
	void testPlayCard() {
		PlayStrategy secondCardStrategy = (hand, tableCard, briscola) -> 1;
        Player customPlayer = new Player("Custom", secondCardStrategy);

        customPlayer.receiveCard(assoDenari);
        customPlayer.receiveCard(treCoppe);
        
        //non ci sono carte sul tavolo, quindi tableCard = null
        BriscolaCard played = customPlayer.playCard(null, Suit.DENARI);

        assertThat(played).isEqualTo(treCoppe);
        assertThat(customPlayer.getHand())
        	.hasSize(1).containsExactly(assoDenari);
        }
	
	@Test
	void testPlayCardEmptyHandThrowException() {
		assertThatThrownBy(() -> player.playCard(null, Suit.DENARI))
	        .isInstanceOf(IllegalStateException.class)
	        .hasMessageContaining("Impossibile giocare! Mano vuota!");
	}
	
	@Test
	void testPlayCardIndexNotValidThrowException() {
		PlayStrategy invalidStrategy = (hand, tableCard, briscola) -> 99;
        Player badPlayer = new Player("BadPlayer", invalidStrategy);
        badPlayer.receiveCard(assoDenari);

        assertThatThrownBy(() -> badPlayer.playCard(null, Suit.DENARI))
                .isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	void testCollectCards() {
		player.collectCards(assoDenari, treCoppe); // 11 + 10 = 21 pt
		
        assertThat(player.getScore())
        	.isEqualTo(21);

        player.collectCards(dueBastoni,
        		new BriscolaCard(10, Suit.SPADE)); // 0 + 4 = 4 pt
        
        assertThat(player.getScore())
        	.isEqualTo(25);
	}

	@Test
	void testReset() {
		player.receiveCard(assoDenari);
        player.collectCards(treCoppe, dueBastoni);

        player.reset();

        assertThat(player.getHand()).isEmpty();
        assertThat(player.getScore()).isZero();
	}

}
