package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.model.board.NewBoardGame;
import edu.ntnu.iir.bidata.ui.UserInterface;

/**
 * Main class to run the board game.
 */
public class Main {
    public static void main(String[] args) {
        // Create a new game with a board size of 25 and 1 die
        NewBoardGame game = new NewBoardGame(25, 1);
        
        // Add players
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        
        // Start the game
        game.startGame();
        
        // Play until someone wins
        while (!game.isGameOver()) {
            game.makeMove();
        }
        
        // Display the winner
        System.out.println("Game Over! Winner: " + game.getWinner().getName());
    }
}