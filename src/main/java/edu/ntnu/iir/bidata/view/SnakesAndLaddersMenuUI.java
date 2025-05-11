package edu.ntnu.iir.bidata.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;

public class SnakesAndLaddersMenuUI {
    private final Stage primaryStage;
    private final Consumer<List<String>> onStartGame;
    private final BoardManagementUI boardManagementUI;
    /**
     * -- GETTER --
     *  Get the list of selected players
     *
     * @return List of player names
     */
    @Getter
    private List<String> selectedPlayers = new ArrayList<>();
    private Label playerCountLabel;

    /**
     * Creates a new Snakes and Ladders Menu UI
     *
     * @param primaryStage The primary stage
     * @param onStartGame Consumer that accepts the list of selected players when starting the game
     */
    public SnakesAndLaddersMenuUI(Stage primaryStage, Consumer<List<String>> onStartGame) {
        this.primaryStage = primaryStage;
        this.onStartGame = onStartGame;
        this.boardManagementUI = new BoardManagementUI(primaryStage);
        setupMenu();
    }

    private void setupMenu() {
        primaryStage.setTitle("Snakes & Ladders");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5fff5;");

        VBox logoStack = createLogoStack();
        root.setLeft(logoStack);

        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(40, 0, 0, 0));

        StackPane titlePane = new StackPane();
        titlePane.setPrefSize(400, 60);
        titlePane.setStyle("-fx-background-color: #bdebc8; -fx-background-radius: 20;");
        Label titleLabel = new Label("SNAKES & LADDERS");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-family: serif; -fx-font-weight: bold;");
        titlePane.getChildren().add(titleLabel);
        centerBox.getChildren().add(titlePane);

        HBox boardButtons = new HBox(30);
        boardButtons.setAlignment(Pos.CENTER);
        Button newBoardBtn = createMenuButton("NEW BOARD");
        Button loadBoardBtn = createMenuButton("LOAD BOARD");

        newBoardBtn.setOnAction(e -> boardManagementUI.showAddBoardDialog());
        loadBoardBtn.setOnAction(e -> boardManagementUI.showLoadBoardDialog());

        boardButtons.getChildren().addAll(newBoardBtn, loadBoardBtn);
        centerBox.getChildren().add(boardButtons);

        // Choose The Players button
        Button choosePlayersBtn = createMenuButton("Choose The Players");
        choosePlayersBtn.setOnAction(e -> openPlayerSelection());
        centerBox.getChildren().add(choosePlayersBtn);

        // Player count label
        playerCountLabel = new Label("No players selected");
        playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #006400;");
        centerBox.getChildren().add(playerCountLabel);

        // START button
        Button startGameBtn = createMenuButton("START");
        startGameBtn.setOnAction(e -> {
            if (selectedPlayers.size() >= 1) {
                if (onStartGame != null) onStartGame.accept(selectedPlayers);
            } else {
                playerCountLabel.setText("Please select at least one player!");
                playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            }
        });
        centerBox.getChildren().add(startGameBtn);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLogoStack() {
        VBox logoStack = new VBox(8);
        logoStack.setPadding(new Insets(10, 20, 10, 10));
        logoStack.setAlignment(Pos.TOP_LEFT);
        Color[] greens = {
            Color.web("#006400"), Color.web("#008000"), Color.web("#00A000"),
            Color.web("#4caf50"), Color.web("#bdebc8")
        };
        int[] heights = {40, 30, 40, 20, 30, 20, 40, 30, 20, 40, 30};
        for (int i = 0; i < 11; i++) {
            Region r = new Region();
            r.setPrefSize((i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60)), heights[i]);
            r.setStyle("-fx-background-radius: 15; -fx-background-color: " + toHexString(greens[i % greens.length]) + ";");
            logoStack.getChildren().add(r);
        }
        return logoStack;
    }

    private void openPlayerSelection() {
        PlayerSelectionUI playerSelection = new PlayerSelectionUI(primaryStage);
        List<String> players = playerSelection.showAndWait();

        if (players != null && !players.isEmpty()) {
            this.selectedPlayers = players;
            updatePlayerCountLabel();
        }
    }

    private void updatePlayerCountLabel() {
        if (selectedPlayers.isEmpty()) {
            playerCountLabel.setText("No players selected");
        } else {
            playerCountLabel.setText(selectedPlayers.size() + " player(s) selected");
            playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #006400;");
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: #BDEBC8; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 10;");
        return button;
    }
}