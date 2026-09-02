package it.cardgames.briscola.strategy;

import java.util.List;
import java.util.Scanner;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.ItalianSuit;

public class ConsoleInputStrategy implements PlayStrategy {
	
	private final Scanner scanner;
	
	
	
	public ConsoleInputStrategy(Scanner scanner) {
		this.scanner = scanner;
	}



	@Override
	public int chooseCard(List<BriscolaCard> hand, BriscolaCard tableCard, ItalianSuit briscolaSuit) {
		System.out.println("\n----------------------------------------");
        System.out.println("Seme di Briscola regnante: " + briscolaSuit);

        if (tableCard != null) {
            System.out.println("Carta a terra dell'avversario: [" + tableCard + "]");
        } else {
            System.out.println("Sei di prima mano (tavolo vuoto).");
        }

        System.out.println("Le tue carte:");
        for (int i = 0; i < hand.size(); i++) {
            BriscolaCard c = hand.get(i);
            System.out.printf("  [%d] %s%n", i + 1, c);
        }

        int choice = -1;
        while (choice < 0 || choice >= hand.size()) {
            System.out.print("Scegli quale carta giocare (1-" + hand.size() + "): ");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                choice = input - 1;
                if (choice < 0 || choice >= hand.size()) {
                    System.out.println("Numero fuori intervallo. Riprova.");
                }
            } else {
                System.out.println("Input non valido. Inserisci un numero.");
                scanner.next();
            }
        }
        return choice;
    }
		


}
