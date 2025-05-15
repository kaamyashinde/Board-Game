package edu.ntnu.iir.bidata.view.ludo;

import edu.ntnu.iir.bidata.controller.LudoController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import edu.ntnu.iir.bidata.model.Observer;

/**
 * JavaFX UI for the Ludo game board. This class implements the game board interface and game flow
 * for Ludo.
 */
public class LudoGameUI implements Observer {

  private static final Logger LOGGER = Logger.getLogger(LudoGameUI.class.getName());
  private final Stage primaryStage;
  private final List<Player> players;
  private final String[] playerColors = {"Red", "Green", "Yellow", "Blue"};
  private final Color[] colorValues = {
      Color.web("#FF0000"), // Red
      Color.web("#00FF00"), // Green
      Color.web("#FFFF00"), // Yellow
      Color.web("#0000FF")  // Blue
  };
  // Token positions: home positions for each player's 4 tokens
  private final Map<String, List<Circle>> playerTokens = new HashMap<>();
  // Token position tracking
  private final Map<Circle, Integer> tokenPositions = new HashMap<>();
  private final Map<String, int[]> homePositions = new HashMap<>();
  // Path coordinates for each color
  private final Map<String, List<int[]>> pathCoordinates = new HashMap<>();
  // Game state (for UI only)
  private int currentPlayerIndex = 0;
  private int diceValue = 1;
  // UI components
  private BorderPane root;
  private GridPane boardGrid;
  private Button rollDiceButton;
  private Label statusLabel;
  private Label currentPlayerLabel;
  private LudoController controller;
  private Pane tokenLayer;

  /**
   * Creates a new Ludo Game UI with the specified players.
   *
   * @param primaryStage The primary stage to use for the game UI
   * @param players      List of player names
   */
  public LudoGameUI(Stage primaryStage, List<Player> players) {
    LOGGER.info("Initializing Ludo Game UI with players: " + players);
    this.primaryStage = primaryStage;
    // Ensure we have at most 4 players
    this.players = new ArrayList<>(players.subList(0, Math.min(players.size(), 4)));

    initializePathCoordinates();
    initializeHomePositions();
    setupUI();
    initializeTokens();
    updateUI();
  }

  /**
   * Initialize the path coordinates for each player color
   */
  private void initializePathCoordinates() {
    LOGGER.info("Initializing path coordinates for each player color");
    
    // Define the main path coordinates (clockwise from bottom)
    int[][] mainPath = {
      {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}, {5, 6}, {4, 6}, {3, 6}, {2, 6}, {1, 6},
      {0, 6}, {0, 7}, {0, 8}, {1, 8}, {2, 8}, {3, 8}, {4, 8}, {5, 8}, {6, 9}, {6, 10},
      {6, 11}, {6, 12}, {6, 13}, {7, 13}, {8, 13}, {8, 12}, {8, 11}, {8, 10}, {8, 9},
      {9, 8}, {10, 8}, {11, 8}, {12, 8}, {13, 8}, {13, 7}, {13, 6}, {12, 6}, {11, 6},
      {10, 6}, {9, 6}, {8, 5}, {8, 4}, {8, 3}, {8, 2}, {8, 1}, {8, 0}, {7, 0}, {7, 1},
      {7, 2}, {7, 3}, {7, 4}, {7, 5}
    };

    // Define the home stretch coordinates for each color
    int[][] redHomeStretch = {
      {6, 6}, {6, 7}, {6, 8}, {6, 9}, {6, 10}, {6, 11}
    };
    int[][] greenHomeStretch = {
      {7, 8}, {8, 8}, {9, 8}, {10, 8}, {11, 8}, {12, 8}
    };
    int[][] yellowHomeStretch = {
      {8, 7}, {8, 6}, {8, 5}, {8, 4}, {8, 3}, {8, 2}
    };
    int[][] blueHomeStretch = {
      {7, 6}, {6, 6}, {5, 6}, {4, 6}, {3, 6}, {2, 6}
    };

    // Create paths for each color
    for (int i = 0; i < playerColors.length; i++) {
      List<int[]> path = new ArrayList<>();
      String color = playerColors[i];
      
      // Add main path coordinates
      for (int[] coord : mainPath) {
        path.add(coord);
      }
      
      // Add home stretch coordinates based on color
      int[][] homeStretch;
      switch (color) {
        case "Red":
          homeStretch = redHomeStretch;
          break;
        case "Green":
          homeStretch = greenHomeStretch;
          break;
        case "Yellow":
          homeStretch = yellowHomeStretch;
          break;
        case "Blue":
          homeStretch = blueHomeStretch;
          break;
        default:
          homeStretch = redHomeStretch;
      }
      
      for (int[] coord : homeStretch) {
        path.add(coord);
      }
      
      pathCoordinates.put(color, path);
    }
  }

  /**
   * Initialize home positions for each color
   */
  private void initializeHomePositions() {
    LOGGER.info("Initializing home positions for each player color");
    // Home positions for each color (2x2 grid for each player's home area)
    homePositions.put("Red", new int[]{1, 1, 1, 2, 2, 1, 2, 2});     // Bottom-left home area
    homePositions.put("Green", new int[]{1, 11, 1, 12, 2, 11, 2, 12}); // Top-left home area
    homePositions.put("Yellow", new int[]{11, 11, 11, 12, 12, 11, 12, 12}); // Top-right home area
    homePositions.put("Blue", new int[]{11, 1, 11, 2, 12, 1, 12, 2});   // Bottom-right home area
  }

  /**
   * Set up the UI components
   */
  private void setupUI() {
    LOGGER.info("Setting up Ludo game UI");
    primaryStage.setTitle("Ludo Game");

    root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5f5f0;");

    // Top: Status bar
    HBox topBar = setupTopBar();
    root.setTop(topBar);

    // Center: Game board
    StackPane boardContainer = new StackPane();
    boardContainer.setPadding(new Insets(10));

    // Create a 15x15 grid for the board
    boardGrid = new GridPane();
    boardGrid.setAlignment(Pos.CENTER);
    boardGrid.setHgap(1);
    boardGrid.setVgap(1);
    for (int row = 0; row < 15; row++) {
      for (int col = 0; col < 15; col++) {
        StackPane cell = createBoardCell(row, col);
        boardGrid.add(cell, col, row);
      }
    }
    boardGrid.setStyle("-fx-background-color: black; -fx-padding: 2;");
    boardGrid.setEffect(new DropShadow(10, Color.GRAY));

    // Add token layer above the board
    tokenLayer = new Pane();
    tokenLayer.setPickOnBounds(false); // Allow clicks to pass through empty areas
    tokenLayer.setPrefSize(15 * 40, 15 * 40); // 15x15 grid, 40px per cell

    boardContainer.getChildren().addAll(boardGrid, tokenLayer);
    root.setCenter(boardContainer);

    // Right: Controls
    VBox controls = setupControls();
    root.setRight(controls);

    Scene scene = new Scene(root, 900, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Initialize player tokens
   */
  private void initializeTokens() {
    LOGGER.info("Initializing player tokens");
    // Initialize tokens for each player
    for (int i = 0; i < players.size(); i++) {
      String playerName = players.get(i).getName();
      String color = playerColors[i];
      List<Circle> tokens = new ArrayList<>();

      // Create 4 tokens for each player
      for (int j = 0; j < 4; j++) {
        Circle token = new Circle(15);
        token.setFill(colorValues[i]);
        token.setStroke(Color.BLACK);
        token.setStrokeWidth(2);
        token.setEffect(new DropShadow(5, Color.BLACK));
        tokenLayer.getChildren().add(token); // Add to tokenLayer, not grid
        tokens.add(token);
        tokenPositions.put(token, -1); // -1 indicates token is in home
      }
      playerTokens.put(playerName, tokens);
    }
    // Place all tokens in their home positions
    for (int i = 0; i < players.size(); i++) {
      String playerName = players.get(i).getName();
      for (int j = 0; j < 4; j++) {
        updateTokenPosition(playerName, j, -1);
      }
    }
  }

  /**
   * Update the UI to reflect current game state
   */
  private void updateUI() {
    LOGGER.info("Updating UI state");
    String currentPlayerName = players.get(currentPlayerIndex).getName();
    String currentPlayerColor = playerColors[currentPlayerIndex];

    currentPlayerLabel.setText(
        "Current Player: " + currentPlayerName + " (" + currentPlayerColor + ")");
    statusLabel.setText("Roll the dice!");
  }

  /**
   * Set up the top status bar
   */
  private HBox setupTopBar() {
    LOGGER.info("Setting up top status bar");
    HBox topBar = new HBox(20);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);

    // Current player indicator
    currentPlayerLabel = new Label();
    currentPlayerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    // Game status
    statusLabel = new Label("Game started. Roll the dice!");
    statusLabel.setStyle("-fx-font-size: 14px;");

    topBar.getChildren().addAll(currentPlayerLabel, statusLabel);
    return topBar;
  }

  /**
   * Set up the game controls
   */
  private VBox setupControls() {
    VBox controls = new VBox(20);
    controls.setPadding(new Insets(10));
    controls.setAlignment(Pos.TOP_CENTER);
    controls.setPrefWidth(200);

    // Dice roll button
    rollDiceButton = new Button("ROLL DICE");
    rollDiceButton.setPrefWidth(150);
    rollDiceButton.setPrefHeight(60);
    rollDiceButton.setStyle(
        "-fx-background-color: #e8c9ad; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 18px; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 10;"
    );

    rollDiceButton.setOnAction(e -> rollDice());

    // Dice display
    StackPane diceDisplay = new StackPane();
    Rectangle diceBackground = new Rectangle(80, 80);
    diceBackground.setFill(Color.web("#e8c9ad"));
    diceBackground.setArcHeight(15);
    diceBackground.setArcWidth(15);

    Label diceLabel = new Label("1");
    diceLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

    diceDisplay.getChildren().addAll(diceBackground, diceLabel);

    // Add player info
    VBox playerInfo = new VBox(10);
    playerInfo.setAlignment(Pos.TOP_LEFT);

    Label playersTitle = new Label("PLAYERS:");
    playersTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    playerInfo.getChildren().add(playersTitle);

    for (int i = 0; i < players.size(); i++) {
      String playerName = players.get(i).getName();
      String colorName = playerColors[i];

      HBox playerRow = new HBox(10);
      playerRow.setAlignment(Pos.CENTER_LEFT);

      Region colorBox = new Region();
      colorBox.setPrefSize(15, 15);
      colorBox.setStyle("-fx-background-color: " + toHexString(colorValues[i]) + ";");

      Label nameLabel = new Label(playerName + " (" + colorName + ")");

      playerRow.getChildren().addAll(colorBox, nameLabel);
      playerInfo.getChildren().add(playerRow);
    }

    controls.getChildren().addAll(rollDiceButton, diceDisplay, playerInfo);
    return controls;
  }

  /**
   * Create a single board cell
   */
  private StackPane createBoardCell(int row, int col) {
    StackPane cell = new StackPane();
    Rectangle rect = new Rectangle(40, 40);

    // Determine cell color based on position
    if ((row < 6 && col < 6) && !(row > 0 && row < 5 && col > 0 && col < 5)) {
      // Red home area
      rect.setFill(Color.web("#FFCCCC"));
    } else if ((row < 6 && col > 8) && !(row > 0 && row < 5 && col > 9 && col < 14)) {
      // Green home area
      rect.setFill(Color.web("#CCFFCC"));
    } else if ((row > 8 && col < 6) && !(row > 9 && row < 14 && col > 0 && col < 5)) {
      // Yellow home area
      rect.setFill(Color.web("#FFFFCC"));
    } else if ((row > 8 && col > 8) && !(row > 9 && row < 14 && col > 9 && col < 14)) {
      // Blue home area
      rect.setFill(Color.web("#CCCCFF"));
    } else if (isPathCell(row, col)) {
      // Path cells
      rect.setFill(Color.web("#FFFFFF"));
      rect.setStroke(Color.LIGHTGRAY);
      rect.setStrokeWidth(0.5);
    } else if (isSafeCellOrStar(row, col)) {
      // Safe cells or stars
      rect.setFill(Color.web("#E8E8E8"));
      rect.setStroke(Color.LIGHTGRAY);
      rect.setStrokeWidth(0.5);
    } else {
      // Center or other areas
      rect.setFill(Color.web("#F0F0F0"));
    }

    // Add cell to the pane
    cell.getChildren().add(rect);
    return cell;
  }

  /**
   * Roll the dice - UI only
   */
  private void rollDice() {
    rollDiceButton.setDisable(true);

    // Animate dice roll
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.millis(100), event -> {
          diceValue = 1 + (int) (Math.random() * 6);
          updateDiceDisplay();
        })
    );

    timeline.setCycleCount(10);
    timeline.setOnFinished(event -> {
      // Use controller to roll dice and get value
      String currentPlayerName = players.get(currentPlayerIndex).getName();
      diceValue = controller.rollDice();
      updateDiceDisplay();
      // Highlight movable tokens using controller's legal moves
      List<Integer> legalMoves = controller.getLegalMoves(currentPlayerName, diceValue);
      highlightMovableTokens(legalMoves);
      rollDiceButton.setDisable(false);
    });

    timeline.play();
  }

  /**
   * Convert a Color to hex string
   */
  private String toHexString(Color color) {
    return String.format("#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }

  /**
   * Determine if a cell is part of the path
   */
  private boolean isPathCell(int row, int col) {
    // Middle row
    if (row == 7) {
      return col != 7;
    }
    // Middle column
    if (col == 7) {
      return row != 7;
    }
    // Path inside home areas
    return (row == 1 && col >= 1 && col <= 4) ||
        (row >= 1 && row <= 4 && col == 1) ||
        (row == 13 && col >= 10 && col <= 13) ||
        (row >= 10 && row <= 13 && col == 13) ||
        (row == 1 && col >= 10 && col <= 13) ||
        (row >= 1 && row <= 4 && col == 13) ||
        (row == 13 && col >= 1 && col <= 4) ||
        (row >= 10 && row <= 13 && col == 1);
  }

  /**
   * Determine if a cell is a safe cell or star
   */
  private boolean isSafeCellOrStar(int row, int col) {
    // Example safe cells
    return (row == 2 && col == 6) ||
        (row == 6 && col == 12) ||
        (row == 12 && col == 8) ||
        (row == 8 && col == 2);
  }

  /**
   * Update the dice display
   */
  private void updateDiceDisplay() {
    // Find the dice label
    for (javafx.scene.Node node : ((StackPane) rollDiceButton.getParent().getChildrenUnmodifiable()
        .get(1)).getChildren()) {
      if (node instanceof Label) {
        ((Label) node).setText(String.valueOf(diceValue));
        break;
      }
    }
  }

  /**
   * Highlight tokens that can be moved
   */
  private void highlightMovableTokens(List<Integer> legalMoves) {
    String currentPlayerName = players.get(currentPlayerIndex).getName();
    String currentPlayerColor = playerColors[currentPlayerIndex];
    List<Circle> currentPlayerTokens = playerTokens.get(currentPlayerName);
    boolean hasLegalMove = !legalMoves.isEmpty();
    for (int i = 0; i < currentPlayerTokens.size(); i++) {
      Circle token = currentPlayerTokens.get(i);
      if (legalMoves.contains(i)) {
        token.setEffect(new DropShadow(10, Color.WHITE));
        // Add click handler for moving this token
        int tokenIdx = i;
        token.setOnMouseClicked(e -> {
          handleTokenMove(tokenIdx);
        });
      } else {
        token.setEffect(null);
        token.setOnMouseClicked(null);
      }
    }
    if (!hasLegalMove) {
      statusLabel.setText("No legal moves. Passing to next player.");
      Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> nextPlayer()));
      timeline.play();
    } else {
      statusLabel.setText("Select a token to move.");
    }
  }

  /**
   * Handle token move using controller
   */
  private void handleTokenMove(int tokenIndex) {
    if (controller == null) return;

    String currentPlayer = controller.getCurrentPlayerName();
    LudoController.MoveResult result = controller.moveToken(currentPlayer, tokenIndex, diceValue);

    // Update UI based on move result
    switch (result.type) {
      case "home":
        statusLabel.setText(currentPlayer + " moved token " + (tokenIndex + 1) + " out of home");
        break;
      case "finish":
        statusLabel.setText(currentPlayer + "'s token " + (tokenIndex + 1) + " has finished!");
        break;
      case "capture":
        statusLabel.setText(currentPlayer + " captured an opponent's token!");
        break;
      default:
        statusLabel.setText(currentPlayer + " moved token " + (tokenIndex + 1) + " to position " + result.end);
    }

    // Clear highlights and update UI
    clearTokenHighlights();
    controller.setMovingPiece(false);
    update();
  }

  /**
   * Update token position in UI
   */
  private void updateTokenPosition(String playerName, int tokenIndex, int position) {
    List<Circle> tokens = playerTokens.get(playerName);
    if (tokens == null || tokenIndex >= tokens.size()) return;

    Circle token = tokens.get(tokenIndex);
    tokenPositions.put(token, position);

    if (position == -1) {
      // Token is in home
      int[] homeCoords = homePositions.get(playerColors[players.indexOf(players.stream()
          .filter(p -> p.getName().equals(playerName))
          .findFirst()
          .orElse(null))]);
      if (homeCoords != null) {
        int x = homeCoords[tokenIndex * 2] * 40 + 20;
        int y = homeCoords[tokenIndex * 2 + 1] * 40 + 20;
        token.setLayoutX(x);
        token.setLayoutY(y);
      }
    } else if (position >= 100) {
      // Token is finished
      int finishIndex = position - 100;
      String color = playerColors[players.indexOf(players.stream()
          .filter(p -> p.getName().equals(playerName))
          .findFirst()
          .orElse(null))];
      List<int[]> path = pathCoordinates.get(color);
      if (path != null && finishIndex < 6) {
        int[] coords = path.get(path.size() - 6 + finishIndex);
        token.setLayoutX(coords[0] * 40 + 20);
        token.setLayoutY(coords[1] * 40 + 20);
      }
    } else {
      // Token is on the board
      String color = playerColors[players.indexOf(players.stream()
          .filter(p -> p.getName().equals(playerName))
          .findFirst()
          .orElse(null))];
      List<int[]> path = pathCoordinates.get(color);
      if (path != null && position < path.size()) {
        int[] coords = path.get(position);
        token.setLayoutX(coords[0] * 40 + 20);
        token.setLayoutY(coords[1] * 40 + 20);
      }
    }
  }

  /**
   * Remove highlights and click handlers from all tokens
   */
  private void clearTokenHighlights() {
    for (List<Circle> tokens : playerTokens.values()) {
      for (Circle t : tokens) {
        t.setEffect(null);
        t.setOnMouseClicked(null);
      }
    }
  }

  /**
   * Move to the next player (UI update only)
   */
  private void nextPlayer() {
    diceValue = 0; // Reset dice value
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    updateUI();
  }

  /**
   * Sets the game controller
   *
   * @param controller The game controller
   */
  public void setController(LudoController controller) {
    LOGGER.info("Setting game controller");
    this.controller = controller;
  }

  @Override
  public void update() {
    if (controller == null) return;

    // Update current player
    String currentPlayer = controller.getCurrentPlayerName();
    currentPlayerLabel.setText("Current Player: " + currentPlayer);

    // Update dice value
    int[] diceValues = controller.getBoardGame().getCurrentDiceValues();
    if (diceValues != null && diceValues.length > 0) {
      diceValue = diceValues[0];
      updateDiceDisplay();
    }

    // Update token positions
    for (String playerName : players.stream().map(Player::getName).toList()) {
      List<Integer> positions = controller.getPlayerTokenPositions(playerName);
      List<Boolean> home = controller.getPlayerTokenHome(playerName);
      List<Boolean> finished = controller.getPlayerTokenFinished(playerName);

      for (int i = 0; i < positions.size(); i++) {
        if (home.get(i)) {
          updateTokenPosition(playerName, i, -1); // -1 indicates home
        } else if (finished.get(i)) {
          updateTokenPosition(playerName, i, 100 + i); // 100+ indicates finished
        } else {
          updateTokenPosition(playerName, i, positions.get(i));
        }
      }
    }

    // Update UI state
    if (controller.isMovingPiece()) {
      List<Integer> legalMoves = controller.getLegalMoves(currentPlayer, diceValue);
      highlightMovableTokens(legalMoves);
    } else {
      clearTokenHighlights();
    }

    // Check for game over
    if (controller.isGameOver()) {
      statusLabel.setText(currentPlayer + " has won the game!");
      rollDiceButton.setDisable(true);
    }
  }
}