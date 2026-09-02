package it.cardgames.briscola.controller;

import it.cardgames.briscola.model.BriscolaCard;
import it.cardgames.briscola.model.Player;

public record RoundResult(
		Player leadPlayer,
		BriscolaCard leadCard,
		Player followPlayer,
		BriscolaCard followCard,
		Player winner,
		int pointsWon)
{}
