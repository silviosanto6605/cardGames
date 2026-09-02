package it.cardgames.briscola.engine;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.ItalianSuit;

public class BriscolaRuleEngine {

	private BriscolaRuleEngine() {}
	
    public static int getPoints(BriscolaCard card) {
        if (card == null) {
            throw new IllegalArgumentException("La carta non può essere null!");
        }
        return switch (card.rank()) {
            case 1 -> 11; // Asso
            case 3 -> 10; // Tre
            case 10 -> 4; // Re
            case 9 -> 3;  // Cavallo
            case 8 -> 2;  // Fante
            default -> 0; // Lisci: 2, 4, 5, 6, 7
        };
    }

    public static int getPower(BriscolaCard card) {
        if (card == null) {
            throw new IllegalArgumentException("La carta non può essere null!");
        }
        return switch (card.rank()) {
            case 1 -> 10; // Asso
            case 3 -> 9;  // Tre
            case 10 -> 8; // Re
            case 9 -> 7;  // Cavallo
            case 8 -> 6;  // Fante
            case 7 -> 5;
            case 6 -> 4;
            case 5 -> 3;
            case 4 -> 2;
            case 2 -> 1;  // Due
            default -> 0;
        };
    }

    /**
     * @return 0 se prende la prima carta (leadCard), 1 se prende la seconda (followCard)
     */
    public static int evalWinning(BriscolaCard leadCard, BriscolaCard followCard, ItalianSuit briscolaSuit) {
        if (leadCard == null || followCard == null || briscolaSuit == null) {
            throw new IllegalArgumentException("Carte o semi null non ammessi!");
        }

        boolean leadIsBriscola = leadCard.suit() == briscolaSuit;
        boolean followIsBriscola = followCard.suit() == briscolaSuit;

        // Caso 1: una sola è briscola
        if (leadIsBriscola && !followIsBriscola) return 0;
        if (!leadIsBriscola && followIsBriscola) return 1;

        // Caso 2: entrambe briscole
        if (leadIsBriscola && followIsBriscola) {
            return getPower(leadCard) > getPower(followCard) ? 0 : 1;
        }

        // Caso 3: stesso seme non briscola
        if (leadCard.suit() == followCard.suit()) {
            return getPower(leadCard) > getPower(followCard) ? 0 : 1;
        }

        // Caso 4: semi diversi e nessuna briscola
        return 0;
    }
}