package it.cardgames.briscola.model;

import java.util.ArrayList;
import java.util.List;

import it.cardgames.briscola.strategy.PlayStrategy;

public class Player {
	
	private final String name;
	private final List<BriscolaCard> hand = new ArrayList<BriscolaCard>(3);
	private final List<BriscolaCard> collectedCards = new ArrayList<BriscolaCard>();
	private PlayStrategy strategy;
	
	public Player(String name, PlayStrategy strategy) {
		if (name == null || name.isBlank()) 
			throw new IllegalArgumentException("Nome non può essere null o vuoto!");
		
		if(strategy == null) 
			throw new IllegalArgumentException("Strategia di gioco non può essere null!");
		
		this.name = name;
		this.strategy = strategy;
	}
	
	public String getName() {
		return name;
	}
	
	List<BriscolaCard> getHand() {
		return hand;
	}
	

}
