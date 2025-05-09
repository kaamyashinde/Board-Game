package edu.ntnu.iir.bidata.view;

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

/**
 * UI for board management operations including adding, loading and removing board configurations
 */
public class BoardManagementUI {
  private final Stage ownerStage;
  private List<String> boardsList = new ArrayList<>();

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
   * Shows the Add New Board dialog
   */
  public void showAddBoardDialog() {
    Stage addDialog = new Stage();
    addDialog.initOwner(ownerStage);
    addDialog.initModality(Modality.APPLICATION_MODAL);
    addDialog.setTitle("Add New Board");

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: #f5fff5; -fx-background-radius: 20;");

    // Create the green title bar
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(300, 50);
    titlePane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    Label titleLabel = new Label("Add New Board");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    titlePane.getChildren().add(titleLabel);

    // Create name input with green circle marker
    HBox nameBox = new HBox(10);
    nameBox.setAlignment(Pos.CENTER_LEFT);

    Circle greenMarker = new Circle(10, Color.DARKGREEN);
    Label nameLabel = new Label("NAME:");
    nameLabel.setStyle("-fx-font-weight: bold;");

    TextField nameField = new TextField();
    nameField.setPrefWidth(200);

    nameBox.getChildren().addAll(greenMarker, nameLabel, nameField);

    // Add Button
    Button addButton = createStyledButton("ADD", 120, 40);

    // Add components to layout
    layout.getChildren().addAll(titlePane, nameBox, addButton);

    // Handle add button action
    addButton.setOnAction(e -> {
      String boardName = nameField.getText().trim();
      if (!boardName.isEmpty() && !boardsList.contains(boardName)) {
        boardsList.add(boardName);
        addDialog.close();
        // In a real implementation, you would save the board configuration here
      }
    });

    Scene scene = new Scene(layout, 350, 200);
    addDialog.setScene(scene);
    addDialog.showAndWait();
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
    Stage loadDialog = new Stage();
    loadDialog.initOwner(ownerStage);
    loadDialog.initModality(Modality.APPLICATION_MODAL);
    loadDialog.setTitle("Load Board");

    BorderPane layout = new BorderPane();
    layout.setPadding(new Insets(20));
    layout.setStyle("-fx-background-color: #f5fff5; -fx-background-radius: 20;");

    // Create the green title bar
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(300, 50);
    titlePane.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    Label titleLabel = new Label("Load Board");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    titlePane.getChildren().add(titleLabel);

    // Create board list with green circle markers
    VBox boardListBox = new VBox(10);
    boardListBox.setPadding(new Insets(20, 0, 20, 0));
    boardListBox.setAlignment(Pos.CENTER_LEFT);

    // Display each board with a green circle marker
    for (String boardName : boardsList) {
      HBox boardEntry = new HBox(10);
      boardEntry.setAlignment(Pos.CENTER_LEFT);

      Circle marker = new Circle(10, Color.DARKGREEN);
      Label nameLabel = new Label(boardName);
      nameLabel.setStyle("-fx-font-weight: bold;");

      boardEntry.getChildren().addAll(marker, nameLabel);
      boardListBox.getChildren().add(boardEntry);
    }

    // Create bottom buttons (ADD and REMOVE)
    HBox buttonsBox = new HBox(30);
    buttonsBox.setAlignment(Pos.CENTER);

    Button addButton = createStyledButton("ADD", 120, 40);
    Button removeButton = createStyledButton("REMOVE", 120, 40);

    buttonsBox.getChildren().addAll(addButton, removeButton);

    // Handle button actions
    addButton.setOnAction(e -> {
      loadDialog.close();
      showAddBoardDialog();
    });

    removeButton.setOnAction(e -> {
      loadDialog.close();
      showRemoveBoardDialog();
    });

    // Add all components to the layout
    layout.setTop(titlePane);
    BorderPane.setAlignment(titlePane, Pos.CENTER);
    BorderPane.setMargin(titlePane, new Insets(0, 0, 20, 0));

    layout.setCenter(boardListBox);
    layout.setBottom(buttonsBox);
    BorderPane.setMargin(buttonsBox, new Insets(20, 0, 0, 0));

    Scene scene = new Scene(layout, 400, 500);
    loadDialog.setScene(scene);
    loadDialog.showAndWait();
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
}