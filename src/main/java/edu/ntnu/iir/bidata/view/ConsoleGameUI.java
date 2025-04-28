package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

import java.util.Scanner;

/**
 * Console implementation of the GameUI interface.
 */
public class ConsoleGameUI implements GameUI {
    private final Scanner scanner;

    public ConsoleGameUI() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayWelcomeMessage() {
        System.out.println("=== Welcome to the Board Game! ===");
        System.out.println("Get ready to roll the dice and race to the finish!");
        System.out.println("---------------------------------");
    }

    @Override
    public int getNumberOfPlayers() {
        System.out.print("Enter number of players (2-4): ");
        int numPlayers;
        while (true) {
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers >= 2 && numPlayers <= 4) {
                    break;
                }
                System.out.print("Please enter a number between 2 and 4: ");
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return numPlayers;
    }

    @Override
    public String getPlayerName(int playerNumber) {
        System.out.print("Enter name for Player " + playerNumber + ": ");
        return scanner.nextLine();
    }

    @Override
    public void displayPlayerTurn(Player player) {
        System.out.println("\n-----------");
        System.out.println("Current player: " + player.getName());
        System.out.println("Current position: " + player.getCurrentTile().getId());
    }

    @Override
    public void displayDiceRoll(Player player, int rollResult) {
        System.out.println(player.getName() + " rolled: " + rollResult);
    }

    @Override
    public void displayBoard() {
        System.out.println("\nCurrent Board State:");
        System.out.println("-----------");
        // TODO: Implement board visualization
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