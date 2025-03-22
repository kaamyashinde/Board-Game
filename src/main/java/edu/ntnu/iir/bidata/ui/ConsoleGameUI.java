package edu.ntnu.iir.bidata.ui;

import edu.ntnu.iir.bidata.model.Player;

/**
 * Console implementation of the GameUI interface.
 */
public class ConsoleGameUI implements GameUI {
    @Override
    public void displayTurnStart(Player player, int currentPosition) {
        System.out.println("-----------");
        System.out.println("Current player " + player.getName());
        System.out.println("Current position: " + currentPosition);
    }

    @Override
    public void displayDiceRoll(int rollResult) {
        System.out.println("Rolled: " + rollResult);
    }

    @Override
    public void displayNewPosition(int newPosition) {
        System.out.println("New position: " + newPosition);
    }

    @Override
    public void displayWinner(Player winner) {
        System.out.println("Player " + winner.getName() + " has reached the final tile and wins the game!");
    }

    @Override
    public void displaySeparator() {
        System.out.println("-----------");
    }
} 