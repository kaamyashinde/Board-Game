package edu.ntnu.iir.bidata.ui;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

/**
 * Console implementation of the GameUI interface.
 */
public class ConsoleGameUI implements GameUI {
    @Override
    public void displayTurnStart(Player player, int currentPosition) {
        System.out.println("\n-----------");
        System.out.println("Current player: " + player.getName());
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
        System.out.println("\n🎉 Player " + winner.getName() + " has reached the final tile and wins the game! 🎉");
    }

    @Override
    public void displaySeparator() {
        System.out.println("-----------");
    }

    /**
     * Displays information about a tile action that was triggered.
     * @param player The player who triggered the action
     * @param action The action that was triggered
     */
    public void displayTileAction(Player player, TileAction action) {
        System.out.println("\n✨ Special Tile Action! ✨");
        System.out.println("Player: " + player.getName());
        System.out.println("Action: " + action.getDescription());
        System.out.println("-----------");
    }
} 