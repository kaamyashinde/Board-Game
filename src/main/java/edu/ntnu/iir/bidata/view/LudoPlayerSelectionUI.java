package edu.ntnu.iir.bidata.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LudoPlayerSelectionUI {
  private final Stage parentStage;
  private final List<String> selectedPlayers = new ArrayList<>();
  private final List<String> availableColors = new ArrayList<>();
  private final int MAX_PLAYERS = 4;
  private final int MIN_PLAYERS = 2;

  /**
   * Constructor for the player selection UI
   *
   * @param parentStage The parent stage
   */
  public LudoPlayerSelectionUI(Stage parentStage) {
    this.parentStage = parentStage;
    // Initialize color options for Ludo
    availableColors.add("Red");
    availableColors.add("Green");
    availableColors.add("Yellow");
    availableColors.add("Blue");
  }

  /**
   * Shows the player selection dialog and waits for user input
   *
   * @return List of selected player names
   */
  public List<String> showAndWait() {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(parentStage);
    dialog.setTitle("Select Players");
    dialog.setResizable(false);

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5f5f0;");

    // Center: Player selection controls
    VBox content = new VBox(15);
    content.setPadding(new Insets(10));
    content.setAlignment(Pos.TOP_CENTER);

    Label titleLabel = new Label("SELECT 2-4 PLAYERS");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    content.getChildren().add(titleLabel);

    // Player list
    ListView<HBox> playerListView = new ListView<>();
    playerListView.setPrefHeight(200);
    content.getChildren().add(playerListView);

    // Add player controls
    HBox addPlayerControls = new HBox(10);
    addPlayerControls.setAlignment(Pos.CENTER);

    TextField nameField = new TextField();
    nameField.setPromptText("Player Name");
    nameField.setPrefWidth(150);

    ComboBox<String> colorComboBox = new ComboBox<>();
    colorComboBox.getItems().addAll(availableColors);
    colorComboBox.setValue(availableColors.get(0));
    colorComboBox.setPrefWidth(100);

    Button addPlayerBtn = new Button("Add Player");
    addPlayerBtn.setStyle("-fx-background-color: #e8c9ad;");
    addPlayerBtn.setOnAction(e -> {
      if (!nameField.getText().trim().isEmpty() && selectedPlayers.size() < MAX_PLAYERS) {
        String playerName = nameField.getText().trim();
        String selectedColor = colorComboBox.getValue();

        // Add to selected players list
        selectedPlayers.add(playerName);

        // Create display for the player in the list
        HBox playerEntry = new HBox(10);
        playerEntry.setAlignment(Pos.CENTER_LEFT);

        Region colorIndicator = new Region();
        colorIndicator.setPrefSize(20, 20);
        colorIndicator.setStyle("-fx-background-radius: 10; -fx-background-color: " + getColorHexCode(selectedColor) + ";");

        Label nameLabel = new Label(playerName);
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label colorLabel = new Label("(" + selectedColor + ")");

        Button removeBtn = new Button("X");
        removeBtn.setStyle("-fx-background-color: #e8c9ad;");
        removeBtn.setOnAction(event -> {
          playerListView.getItems().remove(playerEntry);
          selectedPlayers.remove(playerName);
          // Make the color available again
          if (!availableColors.contains(selectedColor)) {
            availableColors.add(selectedColor);
            colorComboBox.getItems().add(selectedColor);
          }
        });

        playerEntry.getChildren().addAll(colorIndicator, nameLabel, colorLabel, removeBtn);
        playerListView.getItems().add(playerEntry);

        // Remove the selected color from available options
        availableColors.remove(selectedColor);
        colorComboBox.getItems().remove(selectedColor);
        if (!colorComboBox.getItems().isEmpty()) {
          colorComboBox.setValue(colorComboBox.getItems().get(0));
        }

        nameField.clear();
      }
    });

    addPlayerControls.getChildren().addAll(nameField, colorComboBox, addPlayerBtn);
    content.getChildren().add(addPlayerControls);

    // Button controls at bottom
    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);

    Button okButton = new Button("OK");
    okButton.setPrefWidth(80);
    okButton.setStyle("-fx-background-color: #e8c9ad;");
    okButton.setOnAction(e -> {
      if (selectedPlayers.size() >= MIN_PLAYERS) {
        dialog.close();
      } else {
        showAlert("Not Enough Players", "Please select at least " + MIN_PLAYERS + " players.");
      }
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.setPrefWidth(80);
    cancelButton.setStyle("-fx-background-color: #e8c9ad;");
    cancelButton.setOnAction(e -> {
      selectedPlayers.clear();
      dialog.close();
    });

    buttonBox.getChildren().addAll(okButton, cancelButton);
    content.getChildren().add(buttonBox);

    root.setCenter(content);

    Scene scene = new Scene(root, 500, 400);
    dialog.setScene(scene);
    dialog.showAndWait();

    return new ArrayList<>(selectedPlayers);
  }

  /**
   * Gets the hex code for a color name
   *
   * @param colorName The color name
   * @return Hex code for the color
   */
  private String getColorHexCode(String colorName) {
    switch (colorName) {
      case "Red":
        return "#FF0000";
      case "Green":
        return "#00FF00";
      case "Yellow":
        return "#FFFF00";
      case "Blue":
        return "#0000FF";
      default:
        return "#000000";
    }
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}