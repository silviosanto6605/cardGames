package it.cardgames.briscola.net;

import it.cardgames.briscola.model.BriscolaCard;
import java.util.List;

public record ServerStateDto(
        String type,                 // "INIT", "YOUR_TURN", "WAIT", "ROUND_OVER", "GAME_OVER"
        List<BriscolaCard> myHand,
        BriscolaCard tableCard,
        BriscolaCard groundBriscola,
        int deckSize,
        int myScore,
        int opponentScore,
        boolean isMyTurn,
        String statusMessage
) {}