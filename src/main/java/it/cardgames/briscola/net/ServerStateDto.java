package it.cardgames.briscola.net;

import it.cardgames.briscola.model.BriscolaCard;

import java.util.List;

/**
 * @param type
 * @param myHand
 * @param firstCard
 * @param secondCard
 * @param groundBriscola
 * @param deckSize
 * @param myScore
 * @param opponentScore
 * @param isMyTurn
 * @param lastRoundWonByMe
 * @param statusMessage
 */
public record ServerStateDto(
        String type,                 // "UPDATE", "ROUND_OVER", "DISCONNECT"
        List<BriscolaCard> myHand,
        BriscolaCard firstCard,
        BriscolaCard secondCard,
        BriscolaCard groundBriscola,
        int deckSize,
        int myScore,
        int opponentScore,
        boolean isMyTurn,
        Boolean lastRoundWonByMe,    // true se ho preso io, false se l'avversario, null a inizio match
        String statusMessage
) {}