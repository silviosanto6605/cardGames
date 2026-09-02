package it.cardgames.briscola.net;

public record ClientMessage(String action, int cardIndex) {}