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
    layout.setStyle("-fx-background-color: #f5fff5; -fx-background-radius: 20;");

    // Create the green title bar
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(300, 50);
    titlePane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    Label titleLabel = new Label("Remove Board");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    titlePane.getChildren().add(titleLabel);

    // Question label
    Label questionLabel = new Label("Which board would you like to remove?");
    questionLabel.setStyle("-fx-font-weight: bold;");

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
        loadGame(gameName);
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
    button.setStyle(
        "-fx-background-color: #BDEBC8; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 10;"
    );
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

  private void loadBoardFromJson() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Board from JSON");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("JSON Files", "*.json")
    );
    File file = fileChooser.showOpenDialog(ownerStage);
    if (file != null) {
      try {
        currentBoard = new edu.ntnu.iir.bidata.filehandling.board.BoardFileReaderGson().readBoard(file.toPath());
        showAlert("Board loaded from JSON file!");
      } catch (Exception e) {
        showAlert("Error loading board: " + e.getMessage());
      }
    }
  }

  private void saveBoardToJson() {
    // For demo: create a standard board if none loaded
    if (currentBoard == null) {
      currentBoard = edu.ntnu.iir.bidata.model.board.BoardFactory.createStandardBoard(16, new java.util.ArrayList<>());
    }
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Board to JSON");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("JSON Files", "*.json")
    );
    File file = fileChooser.showSaveDialog(ownerStage);
    if (file != null) {
      try {
        new edu.ntnu.iir.bidata.filehandling.board.BoardFileWriterGson().writeBoard(currentBoard, file.toPath());
        showAlert("Board saved to JSON file!");
      } catch (Exception e) {
        showAlert("Error saving board: " + e.getMessage());
      }
    }
  }

  private void loadGame(String gameName) {
    if (gameName != null && !gameName.isEmpty()) {
      try {
        // Get the game type from the file path
        String gameType = gameName.contains("monopoly") ? "monopoly" : "snakesandladders";
        
        // Use the appropriate launcher method based on game type
        if (gameType.equals("monopoly")) {
          JavaFXBoardGameLauncher.getInstance().showMonopolyGameBoardWithLoad(ownerStage, gameName);
        } else {
          JavaFXBoardGameLauncher.getInstance().showSnakesAndLaddersGameBoardWithLoad(ownerStage, gameName);
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading game", e);
      }
    }
  }
}