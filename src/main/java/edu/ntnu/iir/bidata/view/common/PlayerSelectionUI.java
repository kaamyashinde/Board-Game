package edu.ntnu.iir.bidata.view.common;

import java.io.InputStream;
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
 * UI for player selection, including loading players from CSV files and selecting which ones to use
 */
public class PlayerSelectionUI {
  private final Stage stage;
  private final ObservableList<String> availablePlayersList = FXCollections.observableArrayList();
  private final ObservableList<String> selectedPlayersList = FXCollections.observableArrayList();
  private final ListView<String> availablePlayersListView = new ListView<>(availablePlayersList);
  private final ListView<String> selectedPlayersListView = new ListView<>(selectedPlayersList);
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

    // Main content
    VBox centerContent = new VBox(15);
    centerContent.setAlignment(Pos.CENTER);

    // CSV Load button
    Button csvLoadButton = createStyledButton("Load Available Players from CSV", 250, 50);
    centerContent.getChildren().add(csvLoadButton);

    // Two-column layout for available and selected players
    HBox playersContainer = new HBox(20);
    playersContainer.setAlignment(Pos.CENTER);

    // Available players section
    VBox availableSection = new VBox(10);
    availableSection.setAlignment(Pos.CENTER);

    Label availableLabel = new Label("Available Players");
    availableLabel.getStyleClass().add("player-selection-section-label");

    availablePlayersListView.setPrefHeight(200);
    availablePlayersListView.setPrefWidth(200);
    availablePlayersListView.setCellFactory(lv -> new PlayerListCell());

    Button addPlayerButton = createStyledButton("Add Manual Player", 150, 40);
    Button selectPlayerButton = createStyledButton("Select →", 100, 40);

    availableSection.getChildren().addAll(availableLabel, availablePlayersListView, addPlayerButton, selectPlayerButton);

    // Selected players section
    VBox selectedSection = new VBox(10);
    selectedSection.setAlignment(Pos.CENTER);

    Label selectedLabel = new Label("Selected Players");
    selectedLabel.getStyleClass().add("player-selection-section-label");

    selectedPlayersListView.setPrefHeight(200);
    selectedPlayersListView.setPrefWidth(200);
    selectedPlayersListView.setCellFactory(lv -> new PlayerListCell());

    Button removePlayerButton = createStyledButton("← Remove", 100, 40);
    Button clearAllButton = createStyledButton("Clear All", 100, 40);

    selectedSection.getChildren().addAll(selectedLabel, selectedPlayersListView, removePlayerButton, clearAllButton);

    playersContainer.getChildren().addAll(availableSection, selectedSection);
    centerContent.getChildren().add(playersContainer);

    // Player limit indicators
    HBox limitLabels = new HBox(30);
    limitLabels.setAlignment(Pos.CENTER);

    StackPane maxPlayersPane = new StackPane();
    maxPlayersPane.setPrefSize(200, 50);
    maxPlayersPane.getStyleClass().addAll("player-selection-title-pane", "player-selection-limit-warning");
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
    centerContent.getChildren().add(limitLabels);

    // Status label
    statusLabel = new Label("Load players from CSV or add manually");
    statusLabel.getStyleClass().add("player-selection-status-label");
    centerContent.getChildren().add(statusLabel);

    root.setCenter(centerContent);

    // Button actions
    csvLoadButton.setOnAction(e -> loadPlayersFromCSV());
    addPlayerButton.setOnAction(e -> showAddPlayerDialog());
    selectPlayerButton.setOnAction(e -> selectPlayer());
    removePlayerButton.setOnAction(e -> removeSelectedPlayer());
    clearAllButton.setOnAction(e -> clearAllSelectedPlayers());

    // Done button at the bottom
    Button doneButton = createStyledButton("DONE", 150, 40);
    doneButton.setOnAction(e -> stage.close());

    VBox bottomBox = new VBox(doneButton);
    bottomBox.setAlignment(Pos.CENTER);
    bottomBox.setPadding(new Insets(20, 0, 0, 0));
    root.setBottom(bottomBox);

    Scene scene = new Scene(root, 600, 700);
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
    return new ArrayList<>(selectedPlayersList);
  }

  private void loadPlayersFromCSV() {
    try {
      // Load from the built-in CSV file in resources folder
      String resourcePath = "/saved_players/saved_players.csv";
      InputStream inputStream = getClass().getResourceAsStream(resourcePath);

      if (inputStream == null) {
        statusLabel.setText("Error: Built-in players file not found!");
        return;
      }

      // Use the new method to read directly from InputStream
      java.util.List<edu.ntnu.iir.bidata.model.player.Player> loadedPlayers =
          new edu.ntnu.iir.bidata.filehandling.player.PlayerFileReaderCSV().readPlayersFromInputStream(inputStream);

      // Clear existing available players
      availablePlayersList.clear();

      // Add all loaded players to available list
      for (edu.ntnu.iir.bidata.model.player.Player player : loadedPlayers) {
        String name = player.getName();
        if (!name.isEmpty() && !availablePlayersList.contains(name)) {
          availablePlayersList.add(name);
        }
      }

      inputStream.close();
      statusLabel.setText("Loaded " + availablePlayersList.size() + " available players. Select the ones you want to play with.");

    } catch (Exception e) {
      statusLabel.setText("Error loading built-in CSV: " + e.getMessage());
    }
  }

  private void selectPlayer() {
    String selectedPlayer = availablePlayersListView.getSelectionModel().getSelectedItem();
    if (selectedPlayer != null) {
      if (selectedPlayersList.size() >= 5) {
        statusLabel.setText("Maximum 5 players allowed!");
        return;
      }
      if (!selectedPlayersList.contains(selectedPlayer)) {
        selectedPlayersList.add(selectedPlayer);
        statusLabel.setText("Added " + selectedPlayer + " to selected players");
      } else {
        statusLabel.setText(selectedPlayer + " is already selected!");
      }
    } else {
      statusLabel.setText("Please select a player from the available list first!");
    }
  }

  private void removeSelectedPlayer() {
    String selectedPlayer = selectedPlayersListView.getSelectionModel().getSelectedItem();
    if (selectedPlayer != null) {
      selectedPlayersList.remove(selectedPlayer);
      statusLabel.setText("Removed " + selectedPlayer + " from selected players");
    } else {
      statusLabel.setText("Please select a player from the selected list first!");
    }
  }

  private void clearAllSelectedPlayers() {
    selectedPlayersList.clear();
    statusLabel.setText("Cleared all selected players");
  }

  private void showAddPlayerDialog() {
    Stage addDialog = new Stage();
    addDialog.initOwner(stage);
    addDialog.initModality(Modality.APPLICATION_MODAL);
    addDialog.setTitle("Add New Player");

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getStyleClass().add("player-selection-root");

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
    nameLabel.getStyleClass().add("bold-label");
    TextField nameField = new TextField();
    nameField.setPrefWidth(200);

    nameBox.getChildren().addAll(marker, nameLabel, nameField);

    Button addButton = createStyledButton("ADD", 120, 40);

    layout.getChildren().addAll(titlePane, nameBox, addButton);

    addButton.setOnAction(e -> {
      String name = nameField.getText().trim();
      if (!name.isEmpty()) {
        if (!availablePlayersList.contains(name)) {
          availablePlayersList.add(name);
          statusLabel.setText("Added " + name + " to available players");
          addDialog.close();
        } else {
          statusLabel.setText("Player " + name + " already exists in available players!");
        }
      } else {
        statusLabel.setText("Player name cannot be empty!");
      }
    });

    Scene scene = new Scene(layout, 350, 200);
    scene.getStylesheets().add(getClass().getResource("/common.css").toExternalForm());
    addDialog.setScene(scene);
    addDialog.showAndWait();
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
        nameLabel.getStyleClass().add("bold-label");

        container.getChildren().addAll(playerMarker, nameLabel);
        setGraphic(container);
      }
    }
  }
}