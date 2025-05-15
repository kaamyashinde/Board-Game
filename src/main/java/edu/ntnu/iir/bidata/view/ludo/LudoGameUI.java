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
    // Initialize path coordinates for each color
    // Red path (bottom-left)
    List<int[]> redPath = new ArrayList<>();
    // Add path coordinates here (x,y grid coordinates)
    pathCoordinates.put("Red", redPath);

    // Green path (top-left)
    List<int[]> greenPath = new ArrayList<>();
    // Add path coordinates here
    pathCoordinates.put("Green", greenPath);

    // Yellow path (top-right)
    List<int[]> yellowPath = new ArrayList<>();
    // Add path coordinates here
    pathCoordinates.put("Yellow", yellowPath);

    // Blue path (bottom-right)
    List<int[]> bluePath = new ArrayList<>();
    // Add path coordinates here
    pathCoordinates.put("Blue", bluePath);
  }

  /**
   * Initialize home positions for each color
   */
  private void initializeHomePositions() {
    LOGGER.info("Initializing home positions for each player color");
    // These would be the grid coordinates for each home area
    homePositions.put("Red", new int[]{2, 2, 2, 3, 3, 2, 3, 3});  // Bottom-left home area
    homePositions.put("Green", new int[]{2, 12, 2, 13, 3, 12, 3, 13});  // Top-left home area
    homePositions.put("Yellow", new int[]{12, 12, 12, 13, 13, 12, 13, 13});  // Top-right home area
    homePositions.put("Blue", new int[]{12, 2, 12, 3, 13, 2, 13, 3});  // Bottom-right home area
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
    StackPane boardContainer = setupGameBoard();
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

        // Position token in home area
        int[] homePos = homePositions.get(color);
        token.setTranslateX(homePos[j * 2] * 50);
        token.setTranslateY(homePos[j * 2 + 1] * 50);

        tokens.add(token);
        tokenPositions.put(token, -1); // -1 indicates token is in home
      }

      playerTokens.put(playerName, tokens);
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
   * Set up the game board grid
   */
  private StackPane setupGameBoard() {
    StackPane boardContainer = new StackPane();
    boardContainer.setPadding(new Insets(10));

    // Create a 15x15 grid for the board
    boardGrid = new GridPane();
    boardGrid.setAlignment(Pos.CENTER);
    boardGrid.setHgap(1);
    boardGrid.setVgap(1);

    // Create the board cells
    for (int row = 0; row < 15; row++) {
      for (int col = 0; col < 15; col++) {
        StackPane cell = createBoardCell(row, col);
        boardGrid.add(cell, col, row);
      }
    }

    // Add a border and shadow to the board
    boardGrid.setStyle("-fx-background-color: black; -fx-padding: 2;");
    boardGrid.setEffect(new DropShadow(10, Color.GRAY));

    boardContainer.getChildren().add(boardGrid);
    return boardContainer;
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
    String currentPlayerName = players.get(currentPlayerIndex).getName();
    LudoController.MoveResult result = controller.moveToken(currentPlayerName, tokenIndex, diceValue);
    // Update UI for token move
    updateTokenPosition(currentPlayerName, tokenIndex, result.end);
    // Show message based on move type
    switch (result.type) {
      case "home":
        statusLabel.setText("Token moved out of home!");
        break;
      case "finish":
        statusLabel.setText("Token finished!");
        break;
      case "capture":
        statusLabel.setText("Token captured an opponent! Sent them home.");
        // Optionally, add animation or highlight here
        break;
      default:
        statusLabel.setText("Token moved.");
    }
    // Remove highlights and click handlers
    clearTokenHighlights();
    // Check for win (all tokens finished)
    if (controller.getPlayerTokenFinished(currentPlayerName).stream().allMatch(Boolean::booleanValue)) {
      statusLabel.setText("🏆 " + currentPlayerName + " WINS! 🏆");
      rollDiceButton.setDisable(true);
      return;
    }
    // If dice was 6, allow another roll, else next player
    if (diceValue == 6) {
      statusLabel.setText("Rolled a 6! Roll again.");
    } else {
      nextPlayer();
    }
  }

  /**
   * Update token position in UI
   */
  private void updateTokenPosition(String playerName, int tokenIndex, int position) {
    // Get the token
    List<Circle> tokens = playerTokens.get(playerName);
    if (tokens == null || tokenIndex < 0 || tokenIndex >= tokens.size()) return;
    Circle token = tokens.get(tokenIndex);
    // Home position
    if (position == -1) {
      // Place in home area
      String color = playerColors[currentPlayerIndex];
      int[] homePos = homePositions.get(color);
      int x = homePos[tokenIndex * 2];
      int y = homePos[tokenIndex * 2 + 1];
      placeTokenAtGridPosition(token, x, y);
      return;
    }
    // Main path (0-51)
    String color = playerColors[currentPlayerIndex];
    List<int[]> path = pathCoordinates.get(color);
    if (path != null && position >= 0 && position < path.size()) {
      int[] coords = path.get(position);
      placeTokenAtGridPosition(token, coords[0], coords[1]);
      return;
    }
    // Finish area (optional, if you have finish positions)
    // You can add logic here for finish area if needed
  }

  // Place a token at a specific grid position
  private void placeTokenAtGridPosition(Circle token, int gridX, int gridY) {
    LOGGER.info(String.format("Placing token at grid position (%d, %d)", gridX, gridY));
    // Find the cell at the grid position
    StackPane cell = null;
    for (javafx.scene.Node node : boardGrid.getChildren()) {
      if (GridPane.getColumnIndex(node) == gridX && GridPane.getRowIndex(node) == gridY) {
        cell = (StackPane) node;
        break;
      }
    }
    if (cell != null) {
      // Remove from old cell
      StackPane currentCell = (StackPane) token.getParent();
      if (currentCell != null) {
        currentCell.getChildren().remove(token);
      }
      // Add to new cell
      cell.getChildren().add(token);
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
    updateUI();
    // Optionally, show winner if game is over (if you have such logic)
  }
}