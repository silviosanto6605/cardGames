package it.cardgames.briscola.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.ItalianSuit;

class WebPlayerStrategyTest {

    private WebPlayerStrategy strategy;
    private List<BriscolaCard> dummyHand;

    @BeforeEach
    void setUp() {
        strategy = new WebPlayerStrategy();
        dummyHand = List.of(
                new BriscolaCard(1, ItalianSuit.DENARI),
                new BriscolaCard(3, ItalianSuit.COPPE),
                new BriscolaCard(7, ItalianSuit.SPADE)
        );
    }

    @Test
    void testChooseCardBlocksUntilSubmitMove() {
        AtomicBoolean callbackExecuted = new AtomicBoolean(false);
        strategy.setOnTurnRequest(tableCard -> callbackExecuted.set(true));

        CompletableFuture<Integer> choiceFuture = CompletableFuture.supplyAsync(
                () -> strategy.chooseCard(dummyHand, null, ItalianSuit.DENARI)
        );

        assertThat(callbackExecuted).isTrue();
        assertThat(choiceFuture.isDone()).isFalse();

        boolean accepted = strategy.submitMove(1);
        assertThat(accepted).isTrue();

        assertThat(choiceFuture.join()).isEqualTo(1);
    }

    @Test
    void testSubmitMoveRejectsInvalidIndex() {
        CompletableFuture<Integer> choiceFuture = CompletableFuture.supplyAsync(
                () -> strategy.chooseCard(dummyHand, null, ItalianSuit.DENARI)
        );

        // Indice negativo e indice oltre la dimensione della mano
        assertThat(strategy.submitMove(-1)).isFalse();
        assertThat(strategy.submitMove(3)).isFalse();
        assertThat(choiceFuture.isDone()).isFalse();

        assertThat(strategy.submitMove(0)).isTrue();
        assertThat(choiceFuture.join()).isZero();
    }

    @Test
    void testAbortMatchThrowsException() throws InterruptedException {
        CountDownLatch insideChooseCard = new CountDownLatch(1);

        strategy.setOnTurnRequest(tableCard -> insideChooseCard.countDown());

        CompletableFuture<Integer> choiceFuture = CompletableFuture.supplyAsync(
                () -> strategy.chooseCard(dummyHand, null, ItalianSuit.DENARI)
        );

        // Attendiamo che il thread sia effettivamente entrato in chooseCard
        boolean reached = insideChooseCard.await(2, TimeUnit.SECONDS);
        assertThat(reached).isTrue();

        strategy.abortMatch("Il giocatore si è disconnesso.");

        assertThatThrownBy(choiceFuture::join)
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Attesa mossa interrotta o client disconnesso");
    }
}