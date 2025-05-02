package edu.ntnu.iir.bidata.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.CubicCurve;
import javafx.stage.Stage;
import edu.ntnu.iir.bidata.view.DiceView;

public class SnakesAndLaddersGameUI {
  private final Stage primaryStage;
  private DiceView diceView;

  public SnakesAndLaddersGameUI(Stage primaryStage) {
    this.primaryStage = primaryStage;
    setupGamePage();
  }

  private void setupGamePage() {
    primaryStage.setTitle("Snakes & Ladders - Game");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5fff5;");

    // --- Board (center) ---
    GridPane boardGrid = new GridPane();
    boardGrid.setAlignment(Pos.CENTER);
    boardGrid.setHgap(2);
    boardGrid.setVgap(2);
    int size = 10; // 10x10 board
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        Rectangle tile = new Rectangle(50, 50);
        tile.setArcWidth(10);
        tile.setArcHeight(10);
        tile.setFill((i + j) % 2 == 0 ? Color.web("#e0ffe0") : Color.web("#7ed957"));
        tile.setStroke(Color.DARKGREEN);
        // Add tile number
        int tileNum = i * size + (i % 2 == 0 ? j + 1 : size - j);
        Label num = new Label(String.valueOf(tileNum));
        num.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        StackPane cell = new StackPane(tile, num);
        boardGrid.add(cell, j, size - 1 - i); // flip rows for bottom-left start
      }
    }
    root.setCenter(boardGrid);

    // --- Right: Player info panel ---
    VBox playerPanel = new VBox(15);
    playerPanel.setPadding(new Insets(20));
    playerPanel.setStyle("-fx-background-color: #e6fff2; -fx-background-radius: 20;");
    playerPanel.setPrefWidth(220);
    playerPanel.setAlignment(Pos.TOP_LEFT);

    for (int i = 1; i <= 5; i++) {
      Label playerLabel = new Label("PLAYER " + i + ":");
      Label posLabel = new Label("at position: ___");
      playerPanel.getChildren().addAll(playerLabel, posLabel);
    }
    root.setRight(playerPanel);

    // --- Top: Roll dice ---
    HBox diceBox = new HBox(20);
    diceBox.setAlignment(Pos.CENTER_LEFT);
    diceBox.setPadding(new Insets(10, 0, 10, 20));
    Button rollDiceBtn = new Button("ROLL DICE");
    rollDiceBtn.setStyle("-fx-background-color: #bdebc8; -fx-font-size: 18px; -fx-background-radius: 15;");
    // Use DiceView instead of emoji label
    diceView = new DiceView();
    diceBox.getChildren().addAll(rollDiceBtn, diceView);
    root.setTop(diceBox);

    Scene scene = new Scene(root, 1100, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
