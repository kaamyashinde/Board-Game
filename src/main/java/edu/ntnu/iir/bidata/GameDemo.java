package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.controller.GameController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.view.ConsoleGameUI;

/**
 * A demo class to demonstrate the board game functionality.
 */
public class GameDemo {
    public static void main(String[] args) {
        try {
            // Create a new game with 2 dice and 25 tiles
            BoardGame boardGame = new BoardGame(2, 25);

            // Create the console UI
            ConsoleGameUI gameUI = new ConsoleGameUI(boardGame);

            // Create the game controller
            GameController gameController = new GameController(boardGame, gameUI);

            // Start the game
            gameController.startGame();
        } catch (Exception e) {
            System.err.println("An error occurred during the game: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 