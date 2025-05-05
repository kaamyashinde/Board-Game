package edu.ntnu.iir.bidata.view;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application that displays a board game simulation.
 */
public class JavaFXBoardGameLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        showMainMenu(primaryStage);
    }

    private void showMainMenu(Stage stage) {
        new MainMenuUI(stage, () -> showSnakesAndLaddersMenu(stage));
    }

    private void showSnakesAndLaddersMenu(Stage stage) {
        new SnakesAndLaddersMenuUI(stage, () -> showGameBoard(stage));
    }

    private void showGameBoard(Stage stage) {
        new SnakesAndLaddersGameUI(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
} 