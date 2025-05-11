package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.model.board.NewBoardGame;
import edu.ntnu.iir.bidata.ui.CommandLineInterface;

/**
 * Main class to run the board game.
 */
public class Main {
    public static void main(String[] args) {
        // Create a new game with a board size of 25 and 1 die
        NewBoardGame game = new NewBoardGame(25, 1);
        
        // Run the command line interface
        CommandLineInterface cli = new CommandLineInterface(game);
        cli.start();
    }
}