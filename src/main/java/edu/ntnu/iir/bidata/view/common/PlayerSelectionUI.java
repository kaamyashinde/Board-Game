package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.filehandling.player.PlayerFileReaderCSV;
import edu.ntnu.iir.bidata.model.player.Player;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * UI for player selection, including loading players from CSV files and selecting which ones to
 * use. Simplified layout with more intuitive workflow.
 */
public class PlayerSelectionUI {
  private final Stage stage;
  private final ObservableList<String> availablePlayersList = FXCollections.observableArrayList();
  private final ObservableList<String> selectedPlayersList = FXCollections.observableArrayList();
  private final ListView<String> availablePlayersListView = new ListView<>(availablePlayersList);
  private final ListView<String> selectedPlayersListView = new ListView<>(selectedPlayersList);
  private final Map<String, String> playerTokenMap = new HashMap<>(); // player name -> token image
  private final List<String> availableTokens =
      new ArrayList<>(
          List.of(
              "token_blue.png",
              "token_green.png",
              "token_purple.png",
              "token_red.png",
              "token_yellow.png"));
  private Label statusLabel;

  /**
   * Creates a new player selection UI overlay.
   *
   * @param owner The owner stage for this dialog
   */
  @Inject
  public PlayerSelectionUI(Stage owner) {
    this.stage = new Stage();
    stage.initOwner(owner);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.UTILITY);
    stage.setTitle("Player Selection");

    setupUI();
  }

  /**
   * Sets up the user interface for the player selection screen.
   *
   * <p>This method initializes and arranges all UI components required for the player selection
   * screen, including: - A root BorderPane layout with padding and styling. - Central content with
   * buttons, list views, and labels for selecting players. - Buttons for loading players from a CSV
   * file and adding players manually, grouped together at the top. - Two list views displaying
   * "Available Players" and "Selected Players" with custom cell factories. - Control buttons for
   * selecting, removing, and clearing players. - Player limit indicators for the minimum and
   * maximum player count. - An instructions label for guiding the user. - A status label for
   * providing feedback to the user. - A "DONE" button for closing the UI.
   *
   * <p>Additionally, the method includes double-click functionality on both list views for quicker
   * player selection and removal. Styling is applied from a CSS file referenced in the scene.
   */
  private void setupUI() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.getStyleClass().add("player-selection-root");

    // Main content
    VBox centerContent = new VBox(15);
    centerContent.setAlignment(Pos.CENTER);

    // Top buttons section - CSV Load and Add Manual Player grouped together
    VBox topButtonsSection = new VBox(15);
    topButtonsSection.setAlignment(Pos.CENTER);

    // CSV Load button
    Button csvLoadButton = createStyledButton("Load Available Players from CSV", 250, 50);
    csvLoadButton.setOnAction(e -> loadPlayersFromCSV());

    // Add manual player button (moved up)
    Button addPlayerButton = createStyledButton("Add Manual Player", 250, 50);
    addPlayerButton.setOnAction(e -> showAddPlayerDialog());

    topButtonsSection.getChildren().addAll(csvLoadButton, addPlayerButton);
    centerContent.getChildren().add(topButtonsSection);

    // Two-column layout for available and selected players
    HBox playersContainer = new HBox(20);
    playersContainer.setAlignment(Pos.CENTER);

    // Available players section
    VBox availableSection = new VBox(10);
    availableSection.setAlignment(Pos.CENTER);

    Label availableLabel = new Label("Available Players");
    availableLabel.getStyleClass().add("player-selection-section-label");
    availableLabel.getStyleClass().add("bold-label");

    availablePlayersListView.setPrefHeight(200);
    availablePlayersListView.setPrefWidth(200);
    availablePlayersListView.setCellFactory(lv -> new PlayerListCell());

    availableSection.getChildren().addAll(availableLabel, availablePlayersListView);

    // Selected players section
    VBox selectedSection = new VBox(10);
    selectedSection.setAlignment(Pos.CENTER);

    Label selectedLabel = new Label("Selected Players");
    selectedLabel.getStyleClass().add("player-selection-section-label");
    selectedLabel.getStyleClass().add("bold-label");

    selectedPlayersListView.setPrefHeight(200);
    selectedPlayersListView.setPrefWidth(200);
    selectedPlayersListView.setCellFactory(lv -> new PlayerListCell());

    selectedSection.getChildren().addAll(selectedLabel, selectedPlayersListView);

    playersContainer.getChildren().addAll(availableSection, selectedSection);
    centerContent.getChildren().add(playersContainer);

    // Control buttons in a row
    HBox controlButtons = new HBox(30);
    controlButtons.setAlignment(Pos.CENTER);

    Button selectPlayerButton = createStyledButton("Select →", 100, 40);
    selectPlayerButton.setOnAction(e -> selectPlayer());

    Button removePlayerButton = createStyledButton("← Remove", 100, 40);
    removePlayerButton.setOnAction(e -> removeSelectedPlayer());

    Button clearAllButton = createStyledButton("Clear All", 100, 40);
    clearAllButton.setOnAction(e -> clearAllSelectedPlayers());

    controlButtons.getChildren().addAll(selectPlayerButton, removePlayerButton, clearAllButton);
    centerContent.getChildren().add(controlButtons);

    // Player limit indicators
    HBox limitLabels = new HBox(30);
    limitLabels.setAlignment(Pos.CENTER);

    StackPane maxPlayersPane = new StackPane();
    maxPlayersPane.setPrefSize(200, 50);
    maxPlayersPane
        .getStyleClass()
        .addAll("player-selection-title-pane", "player-selection-limit-warning");
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

    // Add instructions text
    Label instructionsLabel =
        new Label("Press on the players you want and click \"Select\" to choose them for the game");
    instructionsLabel.getStyleClass().add("player-selection-instructions-label");
    centerContent.getChildren().add(instructionsLabel);

    root.setCenter(centerContent);

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

    // Add double-click functionality for quicker selection
    availablePlayersListView.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            selectPlayer();
          }
        });

    selectedPlayersListView.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            removeSelectedPlayer();
          }
        });
  }

  /**
   * Creates a styled button with specified text, width, and height, and applies a predefined style
   * class to the button.
   *
   * @param text the text to be displayed on the button
   * @param width the preferred width of the button
   * @param height the preferred height of the button
   * @return a Button instance with the specified properties and styles applied
   */
  private Button createStyledButton(String text, int width, int height) {
    Button button = new Button(text);
    button.setPrefWidth(width);
    button.setPrefHeight(height);
    button.getStyleClass().add("player-selection-button");
    return button;
  }

  /**
   * Loads player details from a predefined CSV file located in the resources folder.
   *
   * <p>The method reads the CSV file, parses its contents to create Player objects, and populates
   * the list of available players. Each player's name and optional token image are extracted.
   * Duplicate or empty player names are
   */
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
      java.util.List<Player> loadedPlayers =
          new PlayerFileReaderCSV().readPlayersFromInputStream(inputStream);

      // Clear existing available players
      availablePlayersList.clear();

      // Add all loaded players to available list
      loadedPlayers.forEach(
          player -> {
            String name = player.getName();
            String token = player.getTokenImage();
            if (!name.isEmpty() && !availablePlayersList.contains(name)) {
              availablePlayersList.add(name);
              if (token != null && !token.isEmpty()) {
                playerTokenMap.put(name, token);
              }
            }
          });

      inputStream.close();
      statusLabel.setText(
          "Loaded "
              + availablePlayersList.size()
              + " available players. Select the ones you want to play with.");

    } catch (Exception e) {
      statusLabel.setText("Error loading built-in CSV: " + e.getMessage());
    }
  }

  /**
   * Displays a dialog that allows the user to add a new player with a specified name and token.
   *
   * <p>The dialog consists of the following elements: - A title pane indicating the purpose of the
   * dialog. - A text field for entering the player's name. - A token selection section where tokens
   * not already in use by selected players are displayed as options with associated images. - An
   * "ADD" button to confirm and process the addition of the new player.
   *
   * <p>Validation ensures the entered name is not empty, the selected token is unique among the
   * already selected players, and the name does not conflict with existing available players. If
   * validation passes, the player's name and token are added to the appropriate data structures.
   *
   * <p>The dialog uses modal behavior, blocking interaction with other windows until the dialog is
   * dismissed. Upon adding a player successfully, the dialog is closed automatically.
   *
   * <p>The UI styling is applied using a predefined CSS file and specific style classes. Feedback
   * messages are displayed via a status label when input validation fails or upon success.
   */
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

    // Token selection UI
    Label tokenLabel = new Label("Select Token:");
    tokenLabel.getStyleClass().add("bold-label");
    ToggleGroup tokenGroup = new ToggleGroup();
    HBox tokenBox = new HBox(10);
    tokenBox.setAlignment(Pos.CENTER);
    // Only exclude tokens already used by selected players
    java.util.Set<String> usedTokens = new java.util.HashSet<>();
    selectedPlayersList.forEach(
        selectedPlayer -> {
          String usedToken = playerTokenMap.get(selectedPlayer);
          if (usedToken != null) {
            usedTokens.add(usedToken);
          }
        });
    availableTokens.forEach(
        token -> {
          if (!usedTokens.contains(token)) {
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(tokenGroup);
            ImageView iv =
                new ImageView(new Image(getClass().getResourceAsStream("/tokens/" + token)));
            iv.setFitWidth(32);
            iv.setFitHeight(48);
            rb.setGraphic(iv);
            rb.setUserData(token);
            tokenBox.getChildren().add(rb);
          }
        });

    Button addButton = createStyledButton("ADD", 120, 40);

    layout.getChildren().addAll(titlePane, nameBox, tokenLabel, tokenBox, addButton);

    addButton.setOnAction(
        e -> {
          String name = nameField.getText().trim();
          Toggle selectedToggle = tokenGroup.getSelectedToggle();
          if (name.isEmpty()) {
            statusLabel.setText("Player name cannot be empty!");
            return;
          }
          if (selectedToggle == null) {
            statusLabel.setText("Please select a token!");
            return;
          }
          String token = (String) selectedToggle.getUserData();
          if (availablePlayersList.contains(name)) {
            statusLabel.setText("Player " + name + " already exists in available players!");
            return;
          }
          // Only check for token conflict among selected players
          selectedPlayersList.forEach(
              selectedPlayer -> {
                String usedToken = playerTokenMap.get(selectedPlayer);
                if (token.equals(usedToken)) {
                  statusLabel.setText("Token already taken!");
                  return;
                }
              });
          availablePlayersList.add(name);
          playerTokenMap.put(name, token);
          statusLabel.setText("Added " + name + " to available players with token " + token);
          addDialog.close();
        });

    Scene scene = new Scene(layout, 400, 300);
    scene.getStylesheets().add(getClass().getResource("/common.css").toExternalForm());
    addDialog.setScene(scene);
    addDialog.showAndWait();
  }

  /**
   * Handles the logic for selecting a player from the list of available players.
   *
   * <p>This method allows the user to select a player and add them to the list of selected players,
   * ensuring certain constraints are met: - A maximum of 5 players can be selected at any given
   * time. - Duplicate player selections are not allowed. - If the player is being selected for the
   * first time, a token assignment dialog is displayed.
   *
   * <p>The method performs the following actions: 1. Retrieves the currently selected player from
   * the UI component managing available players. 2. Validates if a player is selected and checks if
   * the player is already in the list of selected players. 3. Ensures that the total number of
   * selected players does not exceed the allowed limit. 4. Displays a token selection dialog for
   * unassigned players or adds the player directly if a token is already assigned. 5. Updates the
   * status label with relevant messages to provide feedback regarding the outcome.
   *
   * <p>Feedback scenarios handled: - If no player is selected, a prompt is displayed asking the
   * user to select a player. - If the selected player is already chosen, a message indicating the
   * duplicate selection is shown. - If the maximum player limit is reached, an error message is
   * displayed. - Confirmation messages are displayed when a player is successfully added to the
   * list.
   *
   * <p>Unique tokens are assigned via a separate dialog and mapped to the selected player's name if
   * necessary.
   */
  private void selectPlayer() {
    String selectedPlayer = availablePlayersListView.getSelectionModel().getSelectedItem();
    if (selectedPlayer != null) {
      if (selectedPlayersList.size() >= 5) {
        statusLabel.setText("Maximum 5 players allowed!");
        return;
      }
      if (!selectedPlayersList.contains(selectedPlayer)) {
        // Prompt for token selection if not already assigned
        if (!playerTokenMap.containsKey(selectedPlayer)) {
          showTokenSelectionDialog(selectedPlayer);
        } else {
          selectedPlayersList.add(selectedPlayer);
          statusLabel.setText("Added " + selectedPlayer + " to selected players");
        }
      } else {
        statusLabel.setText(selectedPlayer + " is already selected!");
      }
    } else {
      statusLabel.setText("Please select a player from the available list first!");
    }
  }

  /**
   * Removes the currently selected player from the list of selected players.
   *
   * <p>This method checks if a player is selected in the selected players list view. If a player is
   * selected, it removes the player from the `selectedPlayersList` and updates the status label to
   * confirm the removal. If no player is selected, the status label prompts the user to select a
   * player first.
   *
   * <p>The method ensures that the user receives feedback about the action taken or the necessary
   * steps to perform the operation correctly.
   */
  private void removeSelectedPlayer() {
    String selectedPlayer = selectedPlayersListView.getSelectionModel().getSelectedItem();
    if (selectedPlayer != null) {
      selectedPlayersList.remove(selectedPlayer);
      statusLabel.setText("Removed " + selectedPlayer + " from selected players");
    } else {
      statusLabel.setText("Please select a player from the selected list first!");
    }
  }

  /**
   * Clears all selected players from the selection list.
   *
   * <p>This method empties the list of currently selected players and updates the status label to
   * indicate that all selected players have been removed. It ensures that the UI remains consistent
   * by reflecting these changes appropriately.
   */
  private void clearAllSelectedPlayers() {
    selectedPlayersList.clear();
    statusLabel.setText("Cleared all selected players");
  }

  /**
   * Displays a dialog for selecting a token for the specified player.
   *
   * <p>The dialog provides a list of available tokens as toggleable options. It ensures that tokens
   * already in use by other players are excluded. A status label is updated with feedback if a
   * token selection is invalid or when the selection is successful. The selected token is mapped to
   * the given player name, and that player is added to the list of selected players. The method
   * uses a modal dialog that blocks interaction with other windows until closed.
   *
   * @param playerName the name of the player for whom a token selection is to be made
   */
  private void showTokenSelectionDialog(String playerName) {
    Stage tokenDialog = new Stage();
    tokenDialog.initOwner(stage);
    tokenDialog.initModality(Modality.APPLICATION_MODAL);
    tokenDialog.setTitle("Select Token for " + playerName);

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getStyleClass().add("player-selection-root");

    Label tokenLabel = new Label("Select Token for " + playerName + ":");
    tokenLabel.getStyleClass().add("bold-label");
    ToggleGroup tokenGroup = new ToggleGroup();
    HBox tokenBox = new HBox(10);
    tokenBox.setAlignment(Pos.CENTER);
    // Only exclude tokens already used by selected players
    java.util.Set<String> usedTokens = new java.util.HashSet<>();
    selectedPlayersList.forEach(
        selectedPlayer -> {
          String usedToken = playerTokenMap.get(selectedPlayer);
          if (usedToken != null) {
            usedTokens.add(usedToken);
          }
        });
    availableTokens.forEach(
        token -> {
          if (!usedTokens.contains(token)) {
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(tokenGroup);
            ImageView iv =
                new ImageView(new Image(getClass().getResourceAsStream("/tokens/" + token)));
            iv.setFitWidth(32);
            iv.setFitHeight(48);
            rb.setGraphic(iv);
            rb.setUserData(token);
            tokenBox.getChildren().add(rb);
          }
        });
    Button selectButton = createStyledButton("SELECT", 120, 40);
    layout.getChildren().addAll(tokenLabel, tokenBox, selectButton);

    selectButton.setOnAction(
        e -> {
          Toggle selectedToggle = tokenGroup.getSelectedToggle();
          if (selectedToggle == null) {
            statusLabel.setText("Please select a token!");
            return;
          }
          String token = (String) selectedToggle.getUserData();
          if (playerTokenMap.containsValue(token)) {
            statusLabel.setText("Token already taken!");
            return;
          }
          playerTokenMap.put(playerName, token);
          selectedPlayersList.add(playerName);
          statusLabel.setText("Added " + playerName + " to selected players with token " + token);
          tokenDialog.close();
        });

    Scene scene = new Scene(layout, 400, 200);
    scene.getStylesheets().add(getClass().getResource("/common.css").toExternalForm());
    tokenDialog.setScene(scene);
    tokenDialog.showAndWait();
  }

  /**
   * Show the player selection dialog.
   *
   * @return List of selected player names
   */
  public List<String> showAndWait() {
    stage.showAndWait();
    return new ArrayList<>(selectedPlayersList);
  }

  /**
   * Retrieves a map that associates player names with their corresponding tokens.
   *
   * @return a new map containing the current player-to-token mappings
   */
  public Map<String, String> getPlayerTokenMap() {
    return new HashMap<>(playerTokenMap);
  }

  /** Custom cell factory for the players list view. */
  private class PlayerListCell extends ListCell<String> {
    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (empty || item == null) {
        setText(null);
        setGraphic(null);
      } else {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        // Default: white fill, black outline
        Color fillColor = Color.WHITE;
        Color strokeColor = Color.BLACK;

        // Try to get the token color from playerTokenMap
        String token = playerTokenMap.get(item);
        if (token != null) {
          switch (token) {
            case "token_red.png" -> fillColor = Color.web("#e74c3c");
            case "token_blue.png" -> fillColor = Color.web("#3498db");
            case "token_green.png" -> fillColor = Color.web("#27ae60");
            case "token_purple.png" -> fillColor = Color.web("#9b59b6");
            case "token_yellow.png" -> fillColor = Color.web("#f1c40f");
            default -> fillColor = Color.WHITE;
          }
        }

        Circle playerMarker = new Circle(10, fillColor);
        playerMarker.setStroke(strokeColor);
        playerMarker.setStrokeWidth(2);

        Label nameLabel = new Label(item);
        nameLabel.getStyleClass().add("bold-label");

        container.getChildren().addAll(playerMarker, nameLabel);
        setGraphic(container);
      }
    }
  }
}
