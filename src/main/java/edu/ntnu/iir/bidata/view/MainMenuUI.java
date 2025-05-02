package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI {
  private final Stage primaryStage;
  private final List<Player> players = new ArrayList<>();
  private final List<String> savedBoards = new ArrayList<>();
  private final int MAX_PLAYERS = 5;
  private final int MIN_PLAYERS = 1;
  private final Runnable onSnakesAndLadders;

  public MainMenuUI(Stage primaryStage, Runnable onSnakesAndLadders) {
    this.primaryStage = primaryStage;
    this.onSnakesAndLadders = onSnakesAndLadders;
    this.savedBoards.add("Default Board");
    setupMainMenu();
  }

  private void setupMainMenu() {
    primaryStage.setTitle("Main Menu");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5fff5;");

    // --- LEFT: PURPLE LOGO STRIP (WITHOUT TEXT) ---
    Color[] purples = {
        Color.web("#2d0066"), Color.web("#4b0082"), Color.web("#6a0dad"),
        Color.web("#7c3aed"), Color.web("#a084e8"), Color.web("#b39ddb"), Color.web("#c3aed6")
    };
    int[] purpleHeights = {40, 60, 40, 30, 20, 40, 30, 20, 40, 30, 20};

    // Create the left logo pane WITHOUT text and WITHOUT clickable functionality
    StackPane leftLogo = createLogoStackPane(purples, purpleHeights, "", null);
    root.setLeft(leftLogo);

    // --- CENTER: "WELCOME" + two rounded rectangles ---
    VBox centerBox = new VBox(30);
    centerBox.setAlignment(Pos.TOP_CENTER);
    centerBox.setPadding(new Insets(40, 0, 0, 0));

    // WELCOME banner
    StackPane welcomePane = new StackPane();
    welcomePane.setPrefSize(400, 60);
    welcomePane.setStyle("-fx-background-color: #bfc2fa; -fx-background-radius: 20;");
    Label welcomeLabel = new Label("WELCOME");
    welcomeLabel.setStyle("-fx-font-size: 30px; -fx-font-family: serif; -fx-font-weight: bold;");
    welcomePane.getChildren().add(welcomeLabel);

    // two placeholders
    HBox menuRow = new HBox(40);
    menuRow.setAlignment(Pos.CENTER);

    // left: board grid placeholder - MAKE THIS THE CLICKABLE ELEMENT
    StackPane boardPane = new StackPane();
    boardPane.setPrefSize(220, 200);
    boardPane.setStyle(
        "-fx-background-color: #c2c2fa; " +
            "-fx-background-radius: 18; " +
            "-fx-border-color: #2e8b57; -fx-border-width: 3; -fx-border-radius: 18;"
    );
    GridPane boardGrid = new GridPane();
    int size = 6;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        Region sq = new Region();
        sq.setPrefSize(20, 20);
        sq.setStyle("-fx-background-color: " + (((i + j) % 2 == 0) ? "#e0ffe0" : "#7ed957") + ";");
        boardGrid.add(sq, j, i);
      }
    }

    // Add a label to the board grid making it clear this is for Snakes & Ladders
    Label snakesAndLaddersLabel = new Label("Snakes & Ladders");
    snakesAndLaddersLabel.setStyle(
        "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: rgba(0, 100, 0, 0.7); " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 10;"
    );

    // Stack the grid and the label
    StackPane boardContent = new StackPane(boardGrid, snakesAndLaddersLabel);
    boardPane.getChildren().add(boardContent);

    // Make the board pane clickable
    boardPane.setOnMouseClicked(e -> {
      if (onSnakesAndLadders != null) {
        onSnakesAndLadders.run();
      }
    });
    boardPane.setCursor(Cursor.HAND);

    // right: empty placeholder
    StackPane emptyPane = new StackPane();
    emptyPane.setPrefSize(220, 200);
    emptyPane.setStyle(
        "-fx-background-color: #c2c2fa; " +
            "-fx-background-radius: 18; " +
            "-fx-border-color: #e69a28; -fx-border-width: 3; -fx-border-radius: 18;"
    );

    menuRow.getChildren().addAll(boardPane, emptyPane);
    centerBox.getChildren().addAll(welcomePane, menuRow);
    root.setCenter(centerBox);

    // --- BOTTOM-RIGHT: credits ---
    Label credit = new Label("©: Durva Parmar & Kamya Shinde");
    credit.setStyle(
        "-fx-font-size: 10px; " +
            "-fx-background-color: #ecebff; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 2 8 2 8;"
    );
    StackPane creditPane = new StackPane(credit);
    creditPane.setAlignment(Pos.BOTTOM_RIGHT);
    root.setBottom(creditPane);

    Scene scene = new Scene(root, 900, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Creates the left‐hand "logo" strip: a vertical stack of colored Regions
   * plus a centered label. If onClick is non‐null, the whole strip is clickable.
   * If text is empty, no label will be displayed.
   */
  private StackPane createLogoStackPane(Color[] colors, int[] heights, String text, Runnable onClick) {
    VBox stack = new VBox(8);
    stack.setPadding(new Insets(10, 20, 10, 10));
    stack.setAlignment(Pos.TOP_LEFT);

    // Only add the title container if text is not empty
    if (text != null && !text.isEmpty()) {
      // Create a container for the title at the top
      VBox titleContainer = new VBox();
      titleContainer.setAlignment(Pos.CENTER);
      titleContainer.setPadding(new Insets(0, 0, 10, 0));

      // Create the title label
      Label titleLabel = new Label(text);
      titleLabel.setStyle(
          "-fx-text-fill: white; " +
              "-fx-font-size: 22px; " +
              "-fx-font-weight: bold; " +
              "-fx-effect: dropshadow(gaussian, #000, 2, 0, 1, 1);"
      );
      titleContainer.getChildren().add(titleLabel);

      // Add title to the top of our stack
      stack.getChildren().add(titleContainer);
    }

    // Then add colored regions
    for (int i = 0; i < heights.length; i++) {
      Region r = new Region();
      int w = (i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60));
      r.setPrefSize(w, heights[i]);
      r.setStyle(
          "-fx-background-radius: 15; " +
              "-fx-background-color: " + toHexString(colors[i % colors.length]) + ";"
      );
      stack.getChildren().add(r);
    }

    StackPane pane = new StackPane(stack);
    pane.setPrefWidth(180);
    pane.setAlignment(Pos.CENTER);

    // Make the entire pane clickable if onClick is provided
    if (onClick != null) {
      pane.setOnMouseClicked(e -> onClick.run());
      pane.setCursor(Cursor.HAND);
    }

    return pane;
  }

  private String toHexString(Color c) {
    return String.format("#%02X%02X%02X",
        (int)(c.getRed() * 255),
        (int)(c.getGreen() * 255),
        (int)(c.getBlue() * 255)
    );
  }

  // --- Remaining dialogs and helpers ---

  private Button createMenuButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(200);
    button.setPrefHeight(50);
    button.setStyle(
        "-fx-background-color: #BDEBC8; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 10;"
    );
    return button;
  }

  private void showNewBoardDialog() {
    Stage dialog = createDialog("Add New Board");

    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    content.setAlignment(Pos.CENTER);

    Label nameLabel = new Label("NAME:");
    nameLabel.setStyle("-fx-font-weight: bold;");

    TextField nameField = new TextField();
    nameField.setPrefWidth(200);

    Button addButton = new Button("Add New Board");
    addButton.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    addButton.setPrefWidth(150);
    addButton.setOnAction(e -> {
      if (!nameField.getText().trim().isEmpty()) {
        savedBoards.add(nameField.getText().trim());
        dialog.close();
      } else {
        showAlert("Invalid Name", "Board name cannot be empty.");
      }
    });

    HBox nameBox = new HBox(10);
    nameBox.setAlignment(Pos.CENTER_LEFT);
    Region indicator = new Region();
    indicator.setPrefSize(20, 80);
    indicator.setStyle("-fx-background-color: #006400; -fx-background-radius: 5;");
    VBox fieldContainer = new VBox(5, nameLabel, nameField);
    nameBox.getChildren().addAll(indicator, fieldContainer);

    content.getChildren().addAll(addButton, nameBox);

    dialog.setScene(new Scene(content, 300, 200));
    dialog.show();
  }

  private void showLoadBoardDialog() {
    Stage dialog = createDialog("Load Board");

    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    content.setAlignment(Pos.CENTER);

    Button loadBtn = new Button("load board");
    loadBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    loadBtn.setPrefWidth(150);

    // list of saved boards
    VBox boardList = new VBox(5);
    boardList.setAlignment(Pos.CENTER_LEFT);
    for (String boardName : savedBoards) {
      HBox boardEntry = new HBox(10);
      boardEntry.setAlignment(Pos.CENTER_LEFT);
      Region ind = new Region();
      ind.setPrefSize(20, 20);
      ind.setStyle("-fx-background-color: #006400; -fx-background-radius: 5;");
      Label name = new Label(boardName);
      name.setStyle("-fx-font-weight: bold;");
      boardEntry.getChildren().addAll(ind, name);
      boardList.getChildren().add(boardEntry);
    }

    Button addBtn = new Button("ADD");
    addBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    addBtn.setPrefWidth(80);
    addBtn.setOnAction(e -> showNewBoardDialog());

    Button removeBtn = new Button("REMOVE");
    removeBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    removeBtn.setPrefWidth(80);
    removeBtn.setOnAction(e -> {
      if (!savedBoards.isEmpty()) {
        savedBoards.remove(savedBoards.size() - 1);
      }
      dialog.close();
    });

    HBox buttonBar = new HBox(20, addBtn, removeBtn);
    buttonBar.setAlignment(Pos.CENTER);

    content.getChildren().addAll(loadBtn, boardList, buttonBar);
    dialog.setScene(new Scene(content, 400, 300));
    dialog.show();
  }

  private void showChoosePlayersDialog() {
    Stage dialog = createDialog("Choose Players");

    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    content.setAlignment(Pos.CENTER);

    ListView<String> playerListView = new ListView<>();
    for (Player p : players) playerListView.getItems().add(p.getName());

    Button addPlayerBtn = new Button("Add Player");
    addPlayerBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    addPlayerBtn.setOnAction(e -> {
      if (players.size() < MAX_PLAYERS) {
        Stage addDialog = showAddPlayerDialog();
        addDialog.setOnHidden(ev -> {
          playerListView.getItems().setAll();
          for (Player pl : players) playerListView.getItems().add(pl.getName());
        });
      } else {
        showAlert("Maximum Players", "You have reached the maximum of " + MAX_PLAYERS);
      }
    });

    Button removePlayerBtn = new Button("Remove Player");
    removePlayerBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    removePlayerBtn.setOnAction(e -> {
      int idx = playerListView.getSelectionModel().getSelectedIndex();
      if (idx >= 0) {
        if (players.size() > MIN_PLAYERS) {
          players.remove(idx);
          playerListView.getItems().remove(idx);
        } else {
          showAlert("Minimum Players", "At least " + MIN_PLAYERS + " required");
        }
      } else {
        showAlert("No Selection", "Select a player first");
      }
    });

    Button doneBtn = new Button("Done");
    doneBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    doneBtn.setOnAction(e -> dialog.close());

    HBox buttonBar = new HBox(20, addPlayerBtn, removePlayerBtn, doneBtn);
    buttonBar.setAlignment(Pos.CENTER);

    content.getChildren().addAll(new Label("Players:"), playerListView, buttonBar);
    dialog.setScene(new Scene(content, 400, 300));
    dialog.show();
  }

  private Stage showAddPlayerDialog() {
    Stage dialog = createDialog("Add New Player");

    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    content.setAlignment(Pos.CENTER);

    Label nameLabel = new Label("NAME:");
    nameLabel.setStyle("-fx-font-weight: bold;");
    TextField nameField = new TextField();
    nameField.setPrefWidth(200);

    Button addBtn = new Button("Add New Player");
    addBtn.setStyle("-fx-background-color: #BDEBC8; -fx-background-radius: 15;");
    addBtn.setPrefWidth(150);
    addBtn.setOnAction(e -> {
      if (!nameField.getText().trim().isEmpty()) {
        players.add(new Player(nameField.getText().trim()));
        dialog.close();
      } else {
        showAlert("Invalid Name", "Player name cannot be empty.");
      }
    });

    HBox nameBox = new HBox(10, new Region() {{
      setPrefSize(20, 80);
      setStyle("-fx-background-color: #006400; -fx-background-radius: 5;");
    }}, new VBox(5, nameLabel, nameField)
    );
    nameBox.setAlignment(Pos.CENTER_LEFT);

    content.getChildren().addAll(addBtn, nameBox);
    dialog.setScene(new Scene(content, 300, 200));
    dialog.show();
    return dialog;
  }

  private Stage createDialog(String title) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(primaryStage);
    dialog.setTitle(title);
    dialog.setResizable(false);
    return dialog;
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void startGame() {
    if (players.isEmpty()) {
      players.add(new Player("Player 1"));
      players.add(new Player("Player 2"));
    }
    BoardGame boardGame = new BoardGame(1, 100);
    players.forEach(boardGame::addPlayer);
    boardGame.initialiseGame();

    // launch your game UI
    new JavaFXGameUI(boardGame).displayWelcomeMessage();
    primaryStage.close();
  }
}