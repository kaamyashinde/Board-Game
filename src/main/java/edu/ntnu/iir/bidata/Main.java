package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.model.NewBoardGame;
import edu.ntnu.iir.bidata.ui.CommandLineInterface;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main class to run the board game.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Board Game application");
        try {
            // Create a new game with a board size of 25 and 1 die
            NewBoardGame game = new NewBoardGame(25, 1);
            LOGGER.info("Game initialized with board size 25 and 1 die");
            
            // Run the command line interface
            CommandLineInterface cli = new CommandLineInterface(game);
            LOGGER.info("Starting Command Line Interface");
            cli.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting the game", e);
        }
    }
}