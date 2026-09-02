package it.cardgames.briscola;

import it.cardgames.briscola.controller.BriscolaMatch;
import it.cardgames.briscola.controller.RoundResult;
import it.cardgames.briscola.model.BriscolaDeck;
import it.cardgames.briscola.model.Player;
import it.cardgames.briscola.strategy.ConsoleInputStrategy;
import it.cardgames.briscola.strategy.PlayStrategy;
import it.cardgames.briscola.strategy.RandomBotStrategy;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PlayStrategy consoleStrategy = new ConsoleInputStrategy(scanner);
        PlayStrategy botStrategy = new RandomBotStrategy();

        System.out.println("=================================================");
        System.out.println("              PARTITA DI BRISCOLA                ");
        System.out.println("=================================================");
        System.out.println("Modalità disponibili:");
        System.out.println(" [1] Umano vs Umano (Hotseat)");
        System.out.println(" [2] Umano vs Bot");
        System.out.print("Seleziona modalità (1 o 2): ");

        int mode = 1;
        if (scanner.hasNextInt()) {
            mode = scanner.nextInt();
        }
        scanner.nextLine();

        System.out.print("\nInserisci nome Giocatore 1: ");
        String name1 = scanner.nextLine().trim();
        if (name1.isEmpty()) name1 = "Giocatore 1";

        Player p1 = new Player(name1, consoleStrategy);
        Player p2;

        if (mode == 2) {
            p2 = new Player("Bot", botStrategy);
            System.out.println("Avversario impostato: " + p2.getName());
        } else {
            System.out.print("Inserisci nome Giocatore 2: ");
            String name2 = scanner.nextLine().trim();
            if (name2.isEmpty()) name2 = "Giocatore 2";
            p2 = new Player(name2, consoleStrategy);
        }

        BriscolaMatch match = new BriscolaMatch(p1, p2, new BriscolaDeck());
        match.startMatch();

        System.out.println("\n-------------------------------------------------");
        System.out.println("Briscola a terra: [" + match.getGroundBriscola() + "]");
        System.out.println("Seme regnante: " + match.getBriscolaSuit());
        System.out.println("-------------------------------------------------");

        int round = 1;
        while (!match.isGameOver()) {
            System.out.printf("%n================ MANO #%d ================", round);
            System.out.printf("%n[Mazzo residuo: %d carte] - Briscola: %s%n", 
                    match.getRemainingCardsInDeck(), match.getGroundBriscola());

            // Esecuzione del round con raccolta dei dettagli
            RoundResult res = match.playRound();

            // Resoconto della giocata
            System.out.println("\n--- Esito della Mano ---");
            System.out.printf("  %s ha giocato: [%s]%n", res.leadPlayer().getName(), res.leadCard());
            System.out.printf("  %s ha risposto: [%s]%n", res.followPlayer().getName(), res.followCard());
            System.out.printf("👉 Prende: %s (+%d pt)%n", res.winner().getName(), res.pointsWon());
            System.out.printf("Punteggi attuali -> %s: %d pt | %s: %d pt%n", 
                    p1.getName(), p1.getScore(), p2.getName(), p2.getScore());
            System.out.println("------------------------");

            round++;
        }

        System.out.println("\n=================================================");
        System.out.println("                 FINE PARTITA                    ");
        System.out.println("=================================================");
        System.out.printf("%s: %d punti%n", p1.getName(), p1.getScore());
        System.out.printf("%s: %d punti%n", p2.getName(), p2.getScore());

        Player winner = match.getWinner();
        if (winner != null) {
            System.out.println("\n🏆 Vincitore: " + winner.getName() + "!");
        } else {
            System.out.println("\n🤝 Pareggio perfetto a 60 punti!");
        }

        scanner.close();
    }
}