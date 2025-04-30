package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.GameController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application that displays a board game simulation.
 */
public class JavaFXBoardGameLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Create a new game with 1 dice and 25 tiles
            BoardGame boardGame = new BoardGame(1, 25);
            
            // Add default players
            boardGame.addPlayer(new Player("Player 1"));
            boardGame.addPlayer(new Player("Player 2"));
            
            // Initialize the game
            boardGame.initialiseGame();
            
            // Create the JavaFX UI
            JavaFXGameUI gameUI = new JavaFXGameUI(boardGame);
            
            // Create the game controller
            GameController gameController = new GameController(boardGame, gameUI);
            
            // Start the game
            gameController.startGame();
        } catch (Exception e) {
            System.err.println("An error occurred during the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 