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
    root.setStyle("-fx-background-color: #f5fff5; -fx-background-radius: 20;");

    // Main player list
    VBox centerContent = new VBox(15);
    centerContent.setAlignment(Pos.CENTER);

    Button csvLoadButton = createStyledButton("load from CSV File", 200, 50);

    playersListView.setPrefHeight(300);
    playersListView.setMaxWidth(300);
    playersListView.setCellFactory(lv -> new PlayerListCell());

    // Player limit indicators
    HBox limitLabels = new HBox(30);
    limitLabels.setAlignment(Pos.CENTER);

    StackPane maxPlayersPane = new StackPane();
    maxPlayersPane.setPrefSize(200, 50);
    maxPlayersPane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15; -fx-border-color: red; -fx-border-radius: 15; -fx-border-width: 2;");
    Label maxPlayersLabel = new Label("MAX 5 PLAYERS");
    maxPlayersLabel.setStyle("-fx-font-weight: bold;");
    maxPlayersPane.getChildren().add(maxPlayersLabel);

    StackPane minPlayersPane = new StackPane();
    minPlayersPane.setPrefSize(200, 50);
    minPlayersPane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    Label minPlayersLabel = new Label("MIN 1 PLAYER");
    minPlayersLabel.setStyle("-fx-font-weight: bold;");
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
    statusLabel.setStyle("-fx-text-fill: #006400; -fx-font-size: 14px;");

    // Add everything to the center content
    centerContent.getChildren().addAll(csvLoadButton, playersListView, limitLabels, playerControls, statusLabel);

    root.setCenter(centerContent);

    // Button actions
    csvLoadButton.setOnAction(e -> loadPlayersFromCSV());
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
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null && count < 5) {
          // Simple CSV parsing - assuming one name per line
          String name = line.trim();
          if (!name.isEmpty() && !playersList.contains(name)) {
            playersList.add(name);
            count++;
          }
        }
        statusLabel.setText("Loaded " + count + " players from CSV");
      } catch (IOException e) {
        statusLabel.setText("Error loading CSV: " + e.getMessage());
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
    layout.setStyle("-fx-background-color: #f5fff5;");

    // Layout similar to the wireframe
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(250, 50);
    titlePane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    Label titleLabel = new Label("Add New Player");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
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
    layout.setStyle("-fx-background-color: #f5fff5;");

    // Layout similar to the wireframe
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(250, 50);
    titlePane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    Label titleLabel = new Label("Remove Player");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
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
    removeDialog.setScene(scene);
    removeDialog.showAndWait();
  }

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