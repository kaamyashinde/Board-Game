package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.ui.UserInterface;

/**
 * Main class to run the board game.
 */
public class Main {
    public static void main(String[] args) {
        // Create and start the user interface
        UserInterface ui = new UserInterface();
        ui.start();
    }
}