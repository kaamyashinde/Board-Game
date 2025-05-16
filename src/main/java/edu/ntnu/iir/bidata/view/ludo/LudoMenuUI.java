package edu.ntnu.iir.bidata.view.ludo;

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

import edu.ntnu.iir.bidata.view.common.BoardManagementUI;
import edu.ntnu.iir.bidata.view.common.MainMenuUI;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import lombok.Getter;

public class LudoMenuUI {
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
   * Creates a new Ludo Menu UI
   *
   * @param primaryStage The primary stage
   * @param onStartGame Consumer that accepts the list of selected players when starting the game
   */
  public LudoMenuUI(Stage primaryStage, Consumer<List<String>> onStartGame) {
    this.primaryStage = primaryStage;
    this.onStartGame = onStartGame;
    this.boardManagementUI = new BoardManagementUI(primaryStage);
    setupMenu();
  }

  private void setupMenu() {
    primaryStage.setTitle("Ludo");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5f5f0;");

    // Create top bar with back button
    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);
    
    Button backButton = new Button("← Back to Main Menu");
    backButton.setStyle("-fx-background-color: #e8c9ad; -fx-font-weight: bold;");
    backButton.setOnAction(e -> {
      JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
    });
    
    topBar.getChildren().add(backButton);
    root.setTop(topBar);

    VBox logoStack = createLogoStack();
    root.setLeft(logoStack);

    VBox centerBox = new VBox(30);
    centerBox.setAlignment(Pos.TOP_CENTER);
    centerBox.setPadding(new Insets(40, 0, 0, 0));

    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(400, 60);
    titlePane.setStyle("-fx-background-color: #e8c9ad; -fx-background-radius: 20;");
    Label titleLabel = new Label("LUDO");
    titleLabel.setStyle("-fx-font-size: 30px; -fx-font-family: serif; -fx-font-weight: bold;");
    titlePane.getChildren().add(titleLabel);
    centerBox.getChildren().add(titlePane);

    HBox boardButtons = new HBox(30);
    boardButtons.setAlignment(Pos.CENTER);
    Button loadBoardBtn = createMenuButton("LOAD BOARD");

    loadBoardBtn.setOnAction(e -> boardManagementUI.showLoadBoardDialog());

    boardButtons.getChildren().add(loadBoardBtn);
    centerBox.getChildren().add(boardButtons);

    // Choose The Players button
    Button choosePlayersBtn = createMenuButton("Choose The Players");
    choosePlayersBtn.setOnAction(e -> openPlayerSelection());
    centerBox.getChildren().add(choosePlayersBtn);

    // Player count label
    playerCountLabel = new Label("No players selected");
    playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #654321;");
    centerBox.getChildren().add(playerCountLabel);

    // START button
    Button startGameBtn = createMenuButton("START");
    startGameBtn.setOnAction(e -> {
      if (selectedPlayers.size() >= 2 && selectedPlayers.size() <= 4) {
        if (onStartGame != null) onStartGame.accept(selectedPlayers);
      } else {
        playerCountLabel.setText("Please select 2-4 players!");
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
    Color[] oranges = {
        Color.web("#CD5C5C"), Color.web("#E9967A"), Color.web("#FFA07A"),
        Color.web("#F08080"), Color.web("#FA8072")
    };
    int[] heights = {40, 30, 40, 20, 30, 20, 40, 30, 20, 40, 30};
    for (int i = 0; i < 11; i++) {
      Region r = new Region();
      r.setPrefSize((i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60)), heights[i]);
      r.setStyle("-fx-background-radius: 15; -fx-background-color: " + toHexString(oranges[i % oranges.length]) + ";");
      logoStack.getChildren().add(r);
    }
    return logoStack;
  }

  private void openPlayerSelection() {
    LudoPlayerSelectionUI playerSelection = new LudoPlayerSelectionUI(primaryStage);
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
      playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #654321;");
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
    button.setStyle("-fx-background-color: #e8c9ad; " +
        "-fx-text-fill: black; " +
        "-fx-font-size: 16px; " +
        "-fx-background-radius: 25; " +
        "-fx-padding: 10;");
    return button;
  }
}