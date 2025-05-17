package edu.ntnu.iir.bidata.view.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UI for player selection, including loading players from CSV files
 */
public class PlayerSelectionUI {
  private final Stage stage;
  private final ObservableList<String> playersList = FXCollections.observableArrayList();
  private final ListView<String> playersListView = new ListView<>(playersList);
  private Label statusLabel;

  /**
   * Creates a new player selection UI overlay.
   *
   * @param owner The owner stage for this dialog
   */
  public PlayerSelectionUI(Stage owner) {
    this.stage = new Stage();
    stage.initOwner(owner);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.UTILITY);
    stage.setTitle("Player Selection");

    setupUI();
  }

  private void setupUI() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.getStyleClass().add("player-selection-root");

    // Main player list
    VBox centerContent = new VBox(15);
    centerContent.setAlignment(Pos.CENTER);

    Button csvLoadButton = createStyledButton("load from CSV File", 200, 50);
    Button csvSaveButton = createStyledButton("save to CSV File", 200, 50);

    playersListView.setPrefHeight(300);
    playersListView.setMaxWidth(300);
    playersListView.setCellFactory(lv -> new PlayerListCell());

    // Player limit indicators
    HBox limitLabels = new HBox(30);
    limitLabels.setAlignment(Pos.CENTER);

    StackPane maxPlayersPane = new StackPane();
    maxPlayersPane.setPrefSize(200, 50);
    maxPlayersPane.getStyleClass().add("player-selection-title-pane");
    maxPlayersPane.setStyle("-fx-border-color: red; -fx-border-radius: 15; -fx-border-width: 2;");
    Label maxPlayersLabel = new Label("MAX 5 PLAYERS");
    maxPlayersLabel.getStyleClass().add("player-selection-title-label");
    maxPlayersPane.getChildren().add(maxPlayersLabel);

    StackPane minPlayersPane = new StackPane();
    minPlayersPane.setPrefSize(200, 50);
    minPlayersPane.getStyleClass().add("player-selection-title-pane");
    Label minPlayersLabel = new Label("MIN 1 PLAYER");
    minPlayersLabel.getStyleClass().add("player-selection-title-label");
    minPlayersPane.getChildren().add(minPlayersLabel);

    limitLabels.getChildren().addAll(maxPlayersPane, minPlayersPane);

    // Player management controls
    HBox playerControls = new HBox(20);
    playerControls.setAlignment(Pos.CENTER);

    Button addPlayerButton = createStyledButton("ADD", 120, 50);
    Button removePlayerButton = createStyledButton("REMOVE", 120, 50);

    playerControls.getChildren().addAll(addPlayerButton, removePlayerButton);

    // Status label for messages
    statusLabel = new Label();
    statusLabel.getStyleClass().add("player-selection-status-label");

    // Add everything to the center content
    centerContent.getChildren().addAll(csvLoadButton, csvSaveButton, playersListView, limitLabels, playerControls, statusLabel);

    root.setCenter(centerContent);

    // Button actions
    csvLoadButton.setOnAction(e -> loadPlayersFromCSV());
    csvSaveButton.setOnAction(e -> savePlayersToCSV());
    addPlayerButton.setOnAction(e -> showAddPlayerDialog());
    removePlayerButton.setOnAction(e -> showRemovePlayerDialog());

    // Done button at the bottom
    Button doneButton = createStyledButton("DONE", 150, 40);
    doneButton.setOnAction(e -> stage.close());

    VBox bottomBox = new VBox(doneButton);
    bottomBox.setAlignment(Pos.CENTER);
    bottomBox.setPadding(new Insets(20, 0, 0, 0));
    root.setBottom(bottomBox);

    Scene scene = new Scene(root, 500, 600);
    scene.getStylesheets().add(getClass().getResource("/common.css").toExternalForm());
    stage.setScene(scene);
  }

  /**
   * Show the player selection dialog
   *
   * @return List of selected player names
   */
  public List<String> showAndWait() {
    stage.showAndWait();
    return new ArrayList<>(playersList);
  }

  private void loadPlayersFromCSV() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Players CSV File");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    );

    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      try {
        java.util.List<edu.ntnu.iir.bidata.model.player.Player> loadedPlayers =
            new edu.ntnu.iir.bidata.filehandling.player.PlayerFileReaderCSV().readPlayers(file.toPath());
        int count = 0;
        for (edu.ntnu.iir.bidata.model.player.Player player : loadedPlayers) {
          String name = player.getName();
          if (!name.isEmpty() && !playersList.contains(name) && count < 5) {
            playersList.add(name);
            count++;
          }
        }
        statusLabel.setText("Loaded " + count + " players from CSV");
      } catch (Exception e) {
        statusLabel.setText("Error loading CSV: " + e.getMessage());
      }
    }
  }

  private void savePlayersToCSV() {
    if (playersList.isEmpty()) {
      statusLabel.setText("No players to save!");
      return;
    }
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Players to CSV File");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    );
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
      try {
        java.util.List<edu.ntnu.iir.bidata.model.player.Player> playersToSave = new java.util.ArrayList<>();
        for (String name : playersList) {
          playersToSave.add(new edu.ntnu.iir.bidata.model.player.Player(name));
        }
        new edu.ntnu.iir.bidata.filehandling.player.PlayerFileWriterCSV().writePlayers(playersToSave, file.toPath());
        statusLabel.setText("Saved " + playersList.size() + " players to CSV");
      } catch (Exception e) {
        statusLabel.setText("Error saving CSV: " + e.getMessage());
      }
    }
  }

  private void showAddPlayerDialog() {
    if (playersList.size() >= 5) {
      statusLabel.setText("Maximum 5 players allowed!");
      return;
    }

    Stage addDialog = new Stage();
    addDialog.initOwner(stage);
    addDialog.initModality(Modality.APPLICATION_MODAL);
    addDialog.setTitle("Add New Player");

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getStyleClass().add("player-selection-root");

    // Layout similar to the wireframe
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(250, 50);
    titlePane.getStyleClass().add("player-selection-title-pane");
    Label titleLabel = new Label("Add New Player");
    titleLabel.getStyleClass().add("player-selection-title-label");
    titlePane.getChildren().add(titleLabel);

    HBox nameBox = new HBox(10);
    nameBox.setAlignment(Pos.CENTER_LEFT);

    Circle marker = new Circle(10, Color.DARKGREEN);

    Label nameLabel = new Label("NAME:");
    nameLabel.setStyle("-fx-font-weight: bold;");

    TextField nameField = new TextField();
    nameField.setPrefWidth(200);

    nameBox.getChildren().addAll(marker, nameLabel, nameField);

    Button addButton = createStyledButton("ADD", 120, 40);

    layout.getChildren().addAll(titlePane, nameBox, addButton);

    addButton.setOnAction(e -> {
      String name = nameField.getText().trim();
      if (!name.isEmpty() && !playersList.contains(name)) {
        playersList.add(name);
        statusLabel.setText("Added player: " + name);
        addDialog.close();
      } else if (name.isEmpty()) {
        statusLabel.setText("Player name cannot be empty!");
      } else {
        statusLabel.setText("Player name already exists!");
      }
    });

    Scene scene = new Scene(layout, 350, 200);
    scene.getStylesheets().add(getClass().getResource("/common.css").toExternalForm());
    addDialog.setScene(scene);
    addDialog.showAndWait();
  }

  private void showRemovePlayerDialog() {
    if (playersList.isEmpty()) {
      statusLabel.setText("No players to remove!");
      return;
    }

    Stage removeDialog = new Stage();
    removeDialog.initOwner(stage);
    removeDialog.initModality(Modality.APPLICATION_MODAL);
    removeDialog.setTitle("Remove Player");

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getStyleClass().add("player-selection-root");

    // Layout similar to the wireframe
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(250, 50);
    titlePane.getStyleClass().add("player-selection-title-pane");
    Label titleLabel = new Label("Remove Player");
    titleLabel.getStyleClass().add("player-selection-title-label");
    titlePane.getChildren().add(titleLabel);

    Label questionLabel = new Label("Which Player would you like to remove?");
    questionLabel.setStyle("-fx-font-weight: bold;");

    Circle marker = new Circle(10, Color.DARKGREEN);

    ComboBox<String> playerComboBox = new ComboBox<>(playersList);
    playerComboBox.setPrefWidth(200);
    if (!playersList.isEmpty()) {
      playerComboBox.setValue(playersList.get(0));
    }

    HBox selectionBox = new HBox(10);
    selectionBox.setAlignment(Pos.CENTER);
    selectionBox.getChildren().addAll(marker, playerComboBox);

    Button removeButton = createStyledButton("REMOVE", 120, 40);

    layout.getChildren().addAll(titlePane, questionLabel, selectionBox, removeButton);

    removeButton.setOnAction(e -> {
      String selected = playerComboBox.getValue();
      if (selected != null) {
        playersList.remove(selected);
        statusLabel.setText("Removed player: " + selected);
        removeDialog.close();
      }
    });

    Scene scene = new Scene(layout, 350, 250);
    scene.getStylesheets().add(getClass().getResource("/common.css").toExternalForm());
    removeDialog.setScene(scene);
    removeDialog.showAndWait();
  }

  private Button createStyledButton(String text, int width, int height) {
    Button button = new Button(text);
    button.setPrefWidth(width);
    button.setPrefHeight(height);
    button.getStyleClass().add("player-selection-button");
    return button;
  }

  /**
   * Custom cell factory for the players list view
   */
  private static class PlayerListCell extends ListCell<String> {
    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (empty || item == null) {
        setText(null);
        setGraphic(null);
      } else {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Circle playerMarker = new Circle(10, Color.DARKGREEN);
        Label nameLabel = new Label(item);
        nameLabel.setStyle("-fx-font-weight: bold;");

        container.getChildren().addAll(playerMarker, nameLabel);
        setGraphic(container);
      }
    }
  }
}