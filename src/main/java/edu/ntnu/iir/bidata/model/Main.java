package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.ui.ConsoleGameUI;

/**
 * Main class to run the board game.
 */
public class Main {
    public static void main(String[] args) {
        // Game configuration
        final int BOARD_SIZE = 20;  // A medium-sized board
        final int NUM_DICE = 1;     // One die for simplicity
        final int NUM_PLAYERS = 2;  // Two players

        // Create the game with console UI
        BoardGame game = new BoardGame(NUM_DICE, NUM_PLAYERS, BOARD_SIZE, new ConsoleGameUI());

        // Create and add players
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");
        
        try {
            game.addPlayer(player1);
            game.addPlayer(player2);
            
            // Display game setup
            System.out.println("=== Board Game Setup ===");
            System.out.println("Board Size: " + BOARD_SIZE);
            System.out.println("Number of Dice: " + NUM_DICE);
            System.out.println("Players:");
            System.out.println("1. " + player1.getName());
            System.out.println("2. " + player2.getName());
            System.out.println("=====================\n");

            // Initialize and start the game
            game.initialiseGame();
            game.playGame();

        } catch (Exception e) {
            System.err.println("Error during game: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 