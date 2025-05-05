package edu.ntnu.iir.bidata.view;

import javafx.application.Application;
import javafx.stage.Stage;

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
        new SnakesAndLaddersMenuUI(stage, () -> showGameBoard(stage));
    }

    /**
     * Displays the actual game board UI.
     * This method initializes the SnakesAndLaddersGameUI which contains
     * the fully implemented game board interface.
     *
     * @param stage The primary stage to show the game on
     */
    private void showGameBoard(Stage stage) {
        // Initialize and display the actual game board UI
        new SnakesAndLaddersGameUI(stage);
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