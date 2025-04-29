package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.view.ConsoleGameUI;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.controller.GameController;

/**
 * Main class to run the board game.
 */
public class Main {
    public static void main(String[] args) {
        // Game configuration
        final int BOARD_SIZE = 100;  // A medium-sized board
        final int NUM_DICE = 1;     // One die for simplicity

        try {
            // Create the game model
            BoardGame game = new BoardGame(NUM_DICE, BOARD_SIZE);
            
            // Create the view
            ConsoleGameUI gameUI = new ConsoleGameUI(game);
            
            // Create the controller
            GameController controller = new GameController(game, gameUI);
            
            // Start the game
            controller.startGame();

        } catch (Exception e) {
            System.err.println("Error during game: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 