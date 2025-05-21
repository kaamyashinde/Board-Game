package edu.ntnu.iir.bidata.view.common;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ntnu.iir.bidata.Inject;

/**
 * UI for board management operations including adding, loading and removing board configurations
 */
public class BoardManagementUI {
  private final Stage ownerStage;
  private List<String> boardsList = new ArrayList<>();
  private edu.ntnu.iir.bidata.model.board.Board currentBoard = null;
  private static final Logger LOGGER = Logger.getLogger(BoardManagementUI.class.getName());

  /**
   * Creates a new board management UI class
   *
   * @param ownerStage The owner stage for this dialog
   */
  @Inject
  public BoardManagementUI(Stage ownerStage) {
    this.ownerStage = ownerStage;
    // For demo purposes, add some sample boards
    boardsList.add("Default Board");
    boardsList.add("Easy Board");
    boardsList.add("Hard Board");
  }

  /**
   * Shows the Remove Board dialog
   */
  public void showRemoveBoardDialog() {
    if (boardsList.isEmpty()) {
      showAlert("No boards available to remove!");
      return;
    }

    Stage removeDialog = new Stage();
    removeDialog.initOwner(ownerStage);
    removeDialog.initModality(Modality.APPLICATION_MODAL);
    removeDialog.setTitle("Remove Board");

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getStyleClass().add("player-selection-root");

    // Create the green title bar
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(300, 50);
    titlePane.getStyleClass().add("player-selection-title-pane");
    Label titleLabel = new Label("Remove Board");
    titleLabel.getStyleClass().add("player-selection-title-label");
    titlePane.getChildren().add(titleLabel);

    // Question label
    Label questionLabel = new Label("Which board would you like to remove?");
    questionLabel.getStyleClass().add("bold-label");

    // Board selection with green circle marker
    HBox selectionBox = new HBox(10);
    selectionBox.setAlignment(Pos.CENTER_LEFT);

    Circle greenMarker = new Circle(10, Color.DARKGREEN);

    ComboBox<String> boardComboBox = new ComboBox<>();
    boardComboBox.getItems().addAll(boardsList);
    boardComboBox.setPrefWidth(200);
    if (!boardsList.isEmpty()) {
      boardComboBox.setValue(boardsList.get(0));
    }

    selectionBox.getChildren().addAll(greenMarker, boardComboBox);

    // Remove Button
    Button removeButton = createStyledButton("REMOVE", 120, 40);

    // Add components to layout
    layout.getChildren().addAll(titlePane, questionLabel, selectionBox, removeButton);

    // Handle remove button action
    removeButton.setOnAction(e -> {
      String selectedBoard = boardComboBox.getValue();
      if (selectedBoard != null) {
        boardsList.remove(selectedBoard);
        removeDialog.close();
        // In a real implementation, you would delete the board configuration here
      }
    });

    Scene scene = new Scene(layout, 350, 200);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    removeDialog.setScene(scene);
    removeDialog.showAndWait();
  }

  /**
   * Shows the Load Board dialog with options to add or remove boards
   */
  public void showLoadBoardDialog() {
    javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
    dialog.setTitle("Load Game");
    dialog.setHeaderText("Select a saved game to load");

    javafx.scene.control.ComboBox<String> gameList = new javafx.scene.control.ComboBox<>();
    gameList.setPromptText("Select a game");
    java.io.File savedGamesDir = new java.io.File("src/main/resources/saved_games");
    if (savedGamesDir.exists() && savedGamesDir.isDirectory()) {
      java.io.File[] savedGames = savedGamesDir.listFiles((dir, name) -> name.endsWith(".json"));
      if (savedGames != null) {
        for (java.io.File game : savedGames) {
          String gameName = game.getName().replace(".json", "");
          gameList.getItems().add(gameName);
        }
      }
    }
    dialog.getDialogPane().setContent(gameList);
    javafx.scene.control.ButtonType loadButtonType = new javafx.scene.control.ButtonType("Load", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loadButtonType, javafx.scene.control.ButtonType.CANCEL);
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == loadButtonType) {
        return gameList.getValue();
      }
      return null;
    });
    java.util.Optional<String> result = dialog.showAndWait();
    result.ifPresent(gameName -> {
      if (gameName != null) {
        //TODO: Load the game
      }
    });
  }

  /**
   * Creates a styled button with consistent appearance
   */
  private Button createStyledButton(String text, int width, int height) {
    Button button = new Button(text);
    button.setPrefWidth(width);
    button.setPrefHeight(height);
    button.getStyleClass().add("player-selection-button");
    return button;
  }

  /**
   * Shows a simple alert message
   */
  private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}