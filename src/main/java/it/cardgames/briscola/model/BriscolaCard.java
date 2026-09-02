package it.cardgames.briscola.model;

public record BriscolaCard(int rank, Suit suit) {
	
	public BriscolaCard {
        if (rank < 1 || rank > 10) {
            throw new IllegalArgumentException("Il valore della carta deve essere tra 1 e 10. Ricevuto: " + rank);
        }
        if (suit == null) {
            throw new IllegalArgumentException("Il seme non può essere null.");
        }
    }
	
	public String getRankName() {
        return switch (rank) {
            case 1 -> "Asso";
            case 8 -> "Fante";
            case 9 -> "Cavallo";
            case 10 -> "Re";
            default -> String.valueOf(rank);
        };
    }

    @Override
    public String toString() {
        return getRankName() + " di " + suit;
    }
}
