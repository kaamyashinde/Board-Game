package edu.ntnu.iir.bidata.view;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * JavaFX application launcher for the Snakes and Ladders UI.
 * This class only handles UI navigation between different screens,
 * with no backend game logic implementation.
 */
public class JavaFXBoardGameLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        showMainMenu(primaryStage);
    }

    /**
     * Displays the main menu UI.
     *
     * @param stage The primary stage to show the menu on
     */
    private void showMainMenu(Stage stage) {
        new MainMenuUI(stage, () -> showSnakesAndLaddersMenu(stage));
    }

    /**
     * Displays the Snakes and Ladders game menu.
     * This screen allows player selection and configuration.
     *
     * @param stage The primary stage to show the menu on
     */
    private void showSnakesAndLaddersMenu(Stage stage) {
        SnakesAndLaddersMenuUI menuUI = new SnakesAndLaddersMenuUI(stage,
            selectedPlayers -> showGameBoard(stage, selectedPlayers));
    }

    /**
     * Displays the actual game board UI.
     * This method initializes the SnakesAndLaddersGameUI which contains
     * the fully implemented game board interface.
     *
     * @param stage The primary stage to show the game on
     * @param players The list of player names to use in the game
     */
    private void showGameBoard(Stage stage, List<String> players) {
        new SnakesAndLaddersGameUI(stage, players);
    }

    /**
     * Main method - entry point for the application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}