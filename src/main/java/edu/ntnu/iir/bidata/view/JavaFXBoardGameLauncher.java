package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.GameController;
import edu.ntnu.iir.bidata.model.NewBoardGame;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * JavaFX application launcher for the board games UI.
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
        new MainMenuUI(stage,
            () -> showSnakesAndLaddersMenu(stage),  // Callback for Snakes and Ladders
            () -> showLudoMenu(stage)               // Callback for Ludo
        );
    }

    /**
     * Displays the Snakes and Ladders game menu.
     * This screen allows player selection and configuration.
     *
     * @param stage The primary stage to show the menu on
     */
    private void showSnakesAndLaddersMenu(Stage stage) {
        SnakesAndLaddersMenuUI menuUI = new SnakesAndLaddersMenuUI(stage,
            selectedPlayers -> showSnakesAndLaddersGameBoard(stage, selectedPlayers));
    }

    /**
     * Displays the Ludo game menu.
     * This screen allows player selection and configuration.
     *
     * @param stage The primary stage to show the menu on
     */
    private void showLudoMenu(Stage stage) {
        LudoMenuUI menuUI = new LudoMenuUI(stage,
            selectedPlayers -> showLudoGameBoard(stage, selectedPlayers));
    }

    /**
     * Displays the Snakes and Ladders game board UI.
     *
     * @param stage The primary stage to show the game on
     * @param players The list of player names to use in the game
     */
    private void showSnakesAndLaddersGameBoard(Stage stage, List<String> players) {
        // Create view
        SnakesAndLaddersGameUI gameUI = new SnakesAndLaddersGameUI(stage, players);

        // Create model (using a default 100-tile board)
        NewBoardGame boardGame = new NewBoardGame(1, 100);

        // Create controller and connect it with the view
        GameController controller = new GameController(boardGame);
        gameUI.setController(controller);

        // Start the game
        controller.startGame();
    }

    /**
     * Displays the Ludo game board UI.
     *
     * @param stage The primary stage to show the game on
     * @param players The list of player names to use in the game
     */
    private void showLudoGameBoard(Stage stage, List<String> players) {
        // Create view
        LudoGameUI gameUI = new LudoGameUI(stage, players);

        // Create model
        NewBoardGame boardGame = new NewBoardGame(1, 52); // Typical Ludo board size

        // Create controller and connect it with the view
        GameController controller = new GameController(boardGame);
        gameUI.setController(controller);

        // Start the game
        controller.startGame();
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