package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SnakesAndLaddersGameUI implements GameUI {
  private final Stage primaryStage;
  private DiceView diceView;
  private final Map<String, Circle> playerTokenMap = new HashMap<>();
  private final Map<String, Label> playerPositionLabels = new HashMap<>();
  private final Map<String, Integer> playerPositions = new HashMap<>();
  private Pane playerLayer;
  private VBox playerPanel;
  private Button rollDiceBtn;
  private Label statusLabel;
  private int currentPlayerIndex = 0;
  private List<String> playerNames;


  private final int TILE_SIZE = 50;
  private final int BOARD_SIZE = 10; // 10x10 board

  // Define snakes and ladders based on your image
  private final int[][] snakes = {
      {99, 41},  // From 99 to 41
      {95, 75},  // From 95 to 75
      {89, 86},  // From 89 to 86
      {78, 15},  // From 78 to 15
      {38, 2},   // From 38 to 2
      {29, 11},  // From 29 to 11
  };

  private final int[][] ladders = {
      {3, 36},   // From 3 to 36
      {8, 12},   // From 8 to 12
      {14, 26},  // From 14 to 26
      {31, 73},  // From 31 to 73
      {59, 80},  // From 59 to 80
      {83, 97},  // From 83 to 97
      {90, 92}   // From 90 to 92
  };

  private final Color[] PLAYER_COLORS = {
      Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE
  };

  /**
   * Constructor that receives the selected players from the menu
   *
   * @param primaryStage The primary stage
   * @param playerNames List of player names selected in the menu
   */
  public SnakesAndLaddersGameUI(Stage primaryStage, List<String> playerNames) {
    this.primaryStage = primaryStage;
    this.playerNames = playerNames;

    // If no players were passed, add a default player
    if (this.playerNames == null || this.playerNames.isEmpty()) {
      this.playerNames = new ArrayList<>();
      this.playerNames.add("Player 1");
    }

    setupGamePage();
    initializePlayerPositions();
    updateCurrentPlayerIndicator();
  }

  /**
   * Legacy constructor that creates its own player selection UI
   * This is kept for backward compatibility
   *
   * @param primaryStage The primary stage
   */
  public SnakesAndLaddersGameUI(Stage primaryStage) {
    this.primaryStage = primaryStage;

    // Get player names through PlayerSelectionUI
    PlayerSelectionUI playerSelection = new PlayerSelectionUI(primaryStage);
    this.playerNames = playerSelection.showAndWait();

    if (playerNames.isEmpty()) {
      // Add a default player if none selected
      playerNames.add("Player 1");
    }

    setupGamePage();
    initializePlayerPositions();
    updateCurrentPlayerIndicator();
  }

  private void setupGamePage() {
    primaryStage.setTitle("Snakes & Ladders - Game");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5fff5;");

    // --- Board (center) ---
    StackPane boardPane = new StackPane();
    boardPane.setAlignment(Pos.CENTER_LEFT);

    // Load custom board image
    Image boardImage = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/snakes_and_ladders_board.jpeg")));
    ImageView boardView = new ImageView(boardImage);
    boardView.setFitWidth(TILE_SIZE * BOARD_SIZE);
    boardView.setFitHeight(TILE_SIZE * BOARD_SIZE);
    boardView.setPreserveRatio(false);

    // Add the board image to the pane
    boardPane.getChildren().add(boardView);

    // Create a pane for player tokens that will be positioned over the board
    playerLayer = new Pane();
    playerLayer.setPrefSize(boardView.getFitWidth(), boardView.getFitHeight());
    boardPane.getChildren().add(playerLayer);

    root.setCenter(boardPane);

    // --- Right: Player info panel ---
    playerPanel = new VBox(15);
    playerPanel.setPadding(new Insets(20));
    playerPanel.setStyle("-fx-background-color: #e6fff2; -fx-background-radius: 20;");
    playerPanel.setPrefWidth(250);
    playerPanel.setAlignment(Pos.TOP_LEFT);

    // Add status label at the top of the player panel
    statusLabel = new Label("Game Started! " + playerNames.get(0) + "'s Turn");
    statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    statusLabel.setWrapText(true);

    playerPanel.getChildren().add(statusLabel);
    playerPanel.getChildren().add(new Label("--------------------"));

    // Create player tokens and labels based on selected players
    for (int i = 0; i < playerNames.size(); i++) {
      String playerName = playerNames.get(i);

      // Create player info section
      VBox playerBox = new VBox(5);
      playerBox.setStyle("-fx-padding: 5px;");

      Label playerLabel = new Label(playerName.toUpperCase() + ":");
      playerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

      // Position label
      Label posLabel = new Label("at position: 0");
      playerPositionLabels.put(playerName, posLabel);

      Circle token = createPlayerToken(i + 1, playerName);
      playerTokenMap.put(playerName, token);

      HBox nameBox = new HBox(10);
      nameBox.setAlignment(Pos.CENTER_LEFT);
      Circle colorIndicator = new Circle(8);
      colorIndicator.setFill(PLAYER_COLORS[i % PLAYER_COLORS.length]);
      colorIndicator.setStroke(Color.BLACK);
      colorIndicator.setStrokeWidth(1);
      nameBox.getChildren().addAll(colorIndicator, playerLabel);

      playerBox.getChildren().addAll(nameBox, posLabel);
      playerPanel.getChildren().add(playerBox);
    }

    root.setRight(playerPanel);

    // --- Top: Roll dice and status ---
    VBox topBox = new VBox(10);
    topBox.setAlignment(Pos.CENTER_LEFT);
    topBox.setPadding(new Insets(10, 0, 10, 20));

    HBox diceBox = new HBox(20);
    diceBox.setAlignment(Pos.CENTER_LEFT);

    rollDiceBtn = new Button("ROLL DICE");
    rollDiceBtn.setStyle("-fx-background-color: #bdebc8; -fx-font-size: 18px; -fx-background-radius: 15;");

    diceView = new DiceView();
    diceBox.getChildren().addAll(rollDiceBtn, diceView);
    rollDiceBtn.setOnAction(e -> rollDiceAndMove());

    topBox.getChildren().add(diceBox);
    root.setTop(topBox);

    Scene scene = new Scene(root, 1100, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Initialize all player positions to the starting position
   */
  private void initializePlayerPositions() {
    for (String playerName : playerNames) {
      playerPositions.put(playerName, 0);
      // Move token to starting position
      movePlayerToken(playerName, 0);
    }
  }

  /**
   * Roll the dice and move the current player
   */
  private void rollDiceAndMove() {
    rollDiceBtn.setDisable(true);

    String currentPlayer = playerNames.get(currentPlayerIndex);

    int roll = 1 + (int)(Math.random() * 6); // Random 1-6
    diceView.setValue(roll);

    statusLabel.setText(currentPlayer + " rolled a " + roll + "!");

    PauseTransition pause = new PauseTransition(Duration.millis(800));
    pause.setOnFinished(event -> {
      updatePlayerPosition(currentPlayer, roll);

      // Check for win condition
      if (playerPositions.get(currentPlayer) == 100) {
        displayWinner(new Player(currentPlayer));
        return;
      }

      // Move to next player
      currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
      updateCurrentPlayerIndicator();

      // Re-enable roll button
      rollDiceBtn.setDisable(false);
    });
    pause.play();
  }

  /**
   * Updates the current player indicator in the UI
   */
  private void updateCurrentPlayerIndicator() {
    String currentPlayer = playerNames.get(currentPlayerIndex);
    statusLabel.setText(currentPlayer + "'s Turn");
  }

  /**
   * Creates a player token with the specified color
   * @param playerNumber the player number (1-based)
   * @param playerName the player's name
   * @return the Circle representing the player token
   */
  private Circle createPlayerToken(int playerNumber, String playerName) {
    Circle token = new Circle(10);
    token.setFill(PLAYER_COLORS[(playerNumber - 1) % PLAYER_COLORS.length]);
    token.setStroke(Color.BLACK);
    token.setStrokeWidth(2);

    // Add token to the player layer
    playerLayer.getChildren().add(token);

    // Position token off-board initially
    token.setTranslateX(20); // off the board
    token.setTranslateY(20); // off the board

    return token;
  }

  /**
   * Updates the position of a player
   * @param playerName the player's name
   * @param diceRoll the dice roll (1-6)
   */
  private void updatePlayerPosition(String playerName, int diceRoll) {
    // Get current position
    int currentPosition = playerPositions.get(playerName);
    int newPosition = currentPosition + diceRoll;

    // Ensure we don't go past 100
    if (newPosition > 100) {
      // Bounce back from 100
      newPosition = 100 - (newPosition - 100);
    }

    // Update position
    playerPositions.put(playerName, newPosition);

    // Update position label
    Label positionLabel = playerPositionLabels.get(playerName);
    positionLabel.setText("at position: " + newPosition);

    movePlayerToken(playerName, newPosition);

    checkSnakesAndLadders(playerName, newPosition);
  }

  /**
   * Moves a player token to a specific position on the board
   * @param playerName the player's name
   * @param position the board position (1-100)
   */
  private void movePlayerToken(String playerName, int position) {
    Circle token = playerTokenMap.get(playerName);
    if (token == null) {
      return;
    }

    if (position == 0) {
      // Starting position (off board)
      token.setTranslateX(20);
      token.setTranslateY(TILE_SIZE * BOARD_SIZE + 20);
      return;
    }

    if (position < 1 || position > 100) {
      return;
    }

    // Calculate coordinates for the position
    int[] coordinates = getCoordinatesForPosition(position);

    // Add a small offset based on player index to prevent complete overlap
    int playerIndex = playerNames.indexOf(playerName);
    int offsetX = playerIndex * 5 - 5;
    int offsetY = playerIndex * 5 - 5;

    // Move the token
    token.setTranslateX(coordinates[0] + offsetX);
    token.setTranslateY(coordinates[1] + offsetY);
  }

  /**
   * Checks if a position has a snake or ladder and updates accordingly
   * @param playerName the player's name
   * @param position the current position
   */
  private void checkSnakesAndLadders(String playerName, int position) {
    int newPosition = position;

    // Check for snakes
    for (int[] snake : snakes) {
      if (snake[0] == position) {
        newPosition = snake[1];
        statusLabel.setText(playerName + " hit a snake! Moving from " + position + " to " + newPosition);
        break;
      }
    }

    // Check for ladders
    for (int[] ladder : ladders) {
      if (ladder[0] == position) {
        newPosition = ladder[1];
        statusLabel.setText(playerName + " found a ladder! Moving from " + position + " to " + newPosition);
        break;
      }
    }

    // If position changed due to snake or ladder, update after a short delay
    if (newPosition != position) {
      PauseTransition pause = new PauseTransition(Duration.millis(1000));
      int finalNewPosition = newPosition;
      pause.setOnFinished(e -> {
        playerPositions.put(playerName, finalNewPosition);

        Label positionLabel = playerPositionLabels.get(playerName);
        positionLabel.setText("at position: " + finalNewPosition);

        movePlayerToken(playerName, finalNewPosition);
      });
      pause.play();
    }
  }

  /**
   * Maps a board position (1-100) to pixel coordinates on the board image
   * @param position the board position (1-100)
   * @return x,y coordinates for the position on the board
   */
  public int[] getCoordinatesForPosition(int position) {
    if (position < 1 || position > 100) {
      throw new IllegalArgumentException("Position must be between 1 and 100");
    }

    int row = (position - 1) / BOARD_SIZE;
    int col;

    // Handle snake and ladder board row alternating direction
    if (row % 2 == 0) { // Even rows (0, 2, 4, 6, 8) go left to right
      col = (position - 1) % BOARD_SIZE;
    } else { // Odd rows (1, 3, 5, 7, 9) go right to left
      col = BOARD_SIZE - 1 - ((position - 1) % BOARD_SIZE);
    }

    // Flip row because the board starts from the bottom
    // FIXED: Adjust the calculation to match the visual layout of the board image
    row = BOARD_SIZE - row;

    // Calculate pixel coordinates (adding offset to center token in tile)
    int x = col * TILE_SIZE + TILE_SIZE / 2;
    int y = row * TILE_SIZE + TILE_SIZE / 2;

    return new int[] {x, y};
  }

  // Implement GameUI interface methods
  @Override
  public void displayWelcomeMessage() {
    statusLabel.setText("Welcome to Snakes & Ladders!");
  }

  @Override
  public int getNumberOfPlayers() {
    return playerNames.size();
  }

  @Override
  public String getPlayerName(int playerNumber) {
    if (playerNumber <= 0 || playerNumber > playerNames.size()) {
      return "Unknown Player";
    }
    return playerNames.get(playerNumber - 1);
  }

  @Override
  public void displayPlayerTurn(Player player) {
    statusLabel.setText(player.getName() + "'s Turn");
  }

  @Override
  public void displayDiceRoll(Player player, int rollResult) {
    statusLabel.setText(player.getName() + " rolled a " + rollResult);
  }

  @Override
  public void displayBoard() {
    // The board is already displayed in the UI
  }

  @Override
  public void displayWinner(Player winner) {
    statusLabel.setText("🏆 " + winner.getName() + " WINS! 🏆");
    rollDiceBtn.setDisable(true);

    // Create a winner announcement
    Stage winnerStage = new Stage();
    winnerStage.initOwner(primaryStage);
    winnerStage.setTitle("Game Over");

    VBox winBox = new VBox(20);
    winBox.setAlignment(Pos.CENTER);
    winBox.setPadding(new Insets(30));
    winBox.setStyle("-fx-background-color: #e6fff2;");

    Label winLabel = new Label(winner.getName() + " WINS!");
    winLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Button okButton = new Button("OK");
    okButton.setStyle("-fx-background-color: #bdebc8; -fx-font-size: 16px;");
    okButton.setOnAction(e -> winnerStage.close());

    winBox.getChildren().addAll(winLabel, okButton);

    Scene winScene = new Scene(winBox, 300, 200);
    winnerStage.setScene(winScene);
    winnerStage.show();
  }

  @Override
  public void displaySeparator() {
  }

  @Override
  public void displayTileAction(Player player, TileAction action) {
    statusLabel.setText(player.getName() + " " + action.getDescription());
  }
}