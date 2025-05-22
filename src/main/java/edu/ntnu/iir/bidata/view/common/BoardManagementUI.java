package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.Inject;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The BoardManagementUI class provides a user interface for managing game boards. It allows users
 * to interact with the system to perform tasks such as removing existing boards through a
 * dialog-based interface.
 */
public class BoardManagementUI {
  private final Stage ownerStage;
  private List<String> boardsList = new ArrayList<>();

  /**
   * Creates a new board management UI class.
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

  /** Shows the Remove Board dialog. */
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
    removeButton.setOnAction(
        e -> {
          String selectedBoard = boardComboBox.getValue();
          if (selectedBoard != null) {
            boardsList.remove(selectedBoard);
            removeDialog.close();
            // In a real implementation, you would delete the board configuration here
          }
        });

    Scene scene = new Scene(layout, 350, 200);
    scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
    removeDialog.setScene(scene);
    removeDialog.showAndWait();
  }

  /** Shows a simple alert message. */
  private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /** Creates a styled button with consistent appearance. */
  private Button createStyledButton(String text, int width, int height) {
    Button button = new Button(text);
    button.setPrefWidth(width);
    button.setPrefHeight(height);
    button.getStyleClass().add("player-selection-button");
    return button;
  }
}
