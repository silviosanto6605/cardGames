package it.cardgames.briscola.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.cardgames.briscola.engine.BriscolaRuleEngine;
import it.cardgames.briscola.strategy.PlayStrategy;

public class Player {
	
	private final String name;
	private final List<BriscolaCard> hand = new ArrayList<>(3);
	private final List<BriscolaCard> collectedCards = new ArrayList<>();
	private PlayStrategy strategy;
	
	public Player(String name, PlayStrategy strategy) {
		if (name == null || name.isBlank()) 
			throw new IllegalArgumentException("Nome non può essere null o vuoto!");
		
		if(strategy == null) 
			throw new IllegalArgumentException("Strategia di gioco non può essere null!");
		
		this.name = name;
		this.strategy = strategy;
	}
	
	
	public void receiveCard(BriscolaCard card) {
		Objects.requireNonNull(card, "La carta non può essere null.");
		hand.add(card);
	}

	
	public BriscolaCard playCard(BriscolaCard tableCard, ItalianSuit briscola) {
		if (hand.isEmpty())
			throw new IllegalStateException("Impossibile giocare! Mano vuota!");
		
		int index = strategy.chooseCard(hand, tableCard, briscola);
		
		if (index<0 || index>=hand.size())
			throw new IndexOutOfBoundsException("Indice non valido!");
		
		return hand.remove(index);
	}
	
	
	public void collectCards(BriscolaCard card1, BriscolaCard card2) {
		if(card1 != null && card2 != null) {
			collectedCards.add(card1);
			collectedCards.add(card2);
		}
	}
	
	
	
	public void reset() {
		hand.clear();
		collectedCards.clear();
	}
	
	public int getScore() {
		return collectedCards.stream()
				.mapToInt(BriscolaRuleEngine::getPoints)
				.sum();
	}
	
	
	
	public String getName() {
		return name;
	}
	
	public void setStrategy(PlayStrategy strategy) {
		this.strategy = strategy;
	}

	
	public List<BriscolaCard> getHand() {
		return hand;
	}
	
	PlayStrategy getStrategy() {
		return strategy;
	}
	
	List<BriscolaCard> getCollectedCards() {
		return collectedCards;
	}
}
