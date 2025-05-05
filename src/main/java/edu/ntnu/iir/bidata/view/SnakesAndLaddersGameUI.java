package edu.ntnu.iir.bidata.view;

import java.util.Objects;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnakesAndLaddersGameUI {
  private final Stage primaryStage;
  private DiceView diceView;
  private final List<Circle> playerTokens = new ArrayList<>();
  private final List<Label> playerPositionLabels = new ArrayList<>();
  private final Map<Integer, Circle> playerTokenMap = new HashMap<>();
  private Pane playerLayer;

  // Board configuration
  private final int TILE_SIZE = 50;
  private final int BOARD_SIZE = 10; // 10x10 board

  // Define snakes and ladders based on your image
  private final int[][] snakes = {
      {99, 78},  // From 99 to 78
      {89, 53},  // From 89 to 53
      {76, 58},  // From 76 to 58
      {66, 45},  // From 66 to 45
      {54, 31},  // From 54 to 31
      {43, 17},  // From 43 to 17
      {36, 6},   // From 36 to 6
      {26, 9}    // From 26 to 9
  };

  private final int[][] ladders = {
      {4, 25},   // From 4 to 25
      {13, 46},  // From 13 to 46
      {33, 49},  // From 33 to 49
      {42, 63},  // From 42 to 63
      {50, 69},  // From 50 to 69
      {62, 81},  // From 62 to 81
      {74, 92}   // From 74 to 92
  };

  // Player colors
  private final Color[] PLAYER_COLORS = {
      Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE
  };

  public SnakesAndLaddersGameUI(Stage primaryStage) {
    this.primaryStage = primaryStage;
    setupGamePage();
  }

  private void setupGamePage() {
    primaryStage.setTitle("Snakes & Ladders - Game");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5fff5;");

    // --- Board (center) ---
    StackPane boardPane = new StackPane();
    boardPane.setAlignment(Pos.CENTER);

    // Load custom board image
    Image boardImage = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/snakes_and_ladders_board.jpeg")));
    ImageView boardView = new ImageView(boardImage);
    boardView.setFitWidth(TILE_SIZE * BOARD_SIZE);
    boardView.setFitHeight(TILE_SIZE * BOARD_SIZE);
    boardView.setPreserveRatio(true);

    // Add the board image to the pane
    boardPane.getChildren().add(boardView);

    // Create a pane for player tokens that will be positioned over the board
    playerLayer = new Pane();
    playerLayer.setPrefSize(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
    boardPane.getChildren().add(playerLayer);

    root.setCenter(boardPane);

    // --- Right: Player info panel ---
    VBox playerPanel = new VBox(15);
    playerPanel.setPadding(new Insets(20));
    playerPanel.setStyle("-fx-background-color: #e6fff2; -fx-background-radius: 20;");
    playerPanel.setPrefWidth(220);
    playerPanel.setAlignment(Pos.TOP_LEFT);

    // Create player tokens and labels
    for (int i = 1; i <= 5; i++) {
      Label playerLabel = new Label("PLAYER " + i + ":");
      Label posLabel = new Label("at position: ___");
      playerPositionLabels.add(posLabel);

      // Create player token
      Circle token = createPlayerToken(i);
      playerTokens.add(token);
      playerTokenMap.put(i, token);

      playerPanel.getChildren().addAll(playerLabel, posLabel);
    }

    root.setRight(playerPanel);

    // --- Top: Roll dice ---
    HBox diceBox = new HBox(20);
    diceBox.setAlignment(Pos.CENTER_LEFT);
    diceBox.setPadding(new Insets(10, 0, 10, 20));
    Button rollDiceBtn = new Button("ROLL DICE");
    rollDiceBtn.setStyle("-fx-background-color: #bdebc8; -fx-font-size: 18px; -fx-background-radius: 15;");

    // Use DiceView instead of emoji label
    diceView = new DiceView();
    diceBox.getChildren().addAll(rollDiceBtn, diceView);

    // Add dice roll functionality
    rollDiceBtn.setOnAction(e -> {
      // Example functionality - roll dice and move player 1
      int roll = 1 + (int)(Math.random() * 6); // Random 1-6
      diceView.setValue(roll);

      // Move player 1 as an example
      PauseTransition pause = new PauseTransition(Duration.millis(500));
      pause.setOnFinished(event -> {
        updatePlayerPosition(1, roll);
      });
      pause.play();
    });

    root.setTop(diceBox);

    Scene scene = new Scene(root, 1100, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Creates a player token with the specified color
   * @param playerNumber the player number (1-5)
   * @return the Circle representing the player token
   */
  private Circle createPlayerToken(int playerNumber) {
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
   * @param playerNumber the player number (1-5)
   * @param diceRoll the dice roll (1-6)
   */
  private void updatePlayerPosition(int playerNumber, int diceRoll) {
    // Get the player's label
    Label positionLabel = playerPositionLabels.get(playerNumber - 1);

    // Get current position
    String posText = positionLabel.getText();
    int currentPosition = 0;

    try {
      // Parse the current position
      if (posText.contains("___")) {
        currentPosition = 0;
      } else {
        currentPosition = Integer.parseInt(posText.substring(posText.lastIndexOf(" ") + 1));
      }
    } catch (Exception e) {
      currentPosition = 0;
    }

    // Calculate new position
    int newPosition = currentPosition + diceRoll;
    if (newPosition > 100) {
      newPosition = 100; // Cap at 100
    }

    // Update position label
    positionLabel.setText("at position: " + newPosition);

    // Move the token to the new position
    movePlayerToken(playerNumber, newPosition);

    // Check for snakes and ladders
    checkSnakesAndLadders(playerNumber, newPosition);
  }

  /**
   * Moves a player token to a specific position on the board
   * @param playerNumber the player number (1-5)
   * @param position the board position (1-100)
   */
  private void movePlayerToken(int playerNumber, int position) {
    Circle token = playerTokenMap.get(playerNumber);
    if (token == null || position < 1 || position > 100) {
      return;
    }

    // Calculate coordinates for the position
    int[] coordinates = getCoordinatesForPosition(position);

    // Add a small offset based on player number to prevent complete overlap
    int offsetX = (playerNumber - 1) * 5 - 5;
    int offsetY = (playerNumber - 1) * 5 - 5;

    // Move the token
    token.setTranslateX(coordinates[0] + offsetX);
    token.setTranslateY(coordinates[1] + offsetY);
  }

  /**
   * Checks if a position has a snake or ladder and updates accordingly
   * @param playerNumber the player number (1-5)
   * @param position the current position
   */
  private void checkSnakesAndLadders(int playerNumber, int position) {
    int newPosition = position;

    // Check for snakes
    for (int[] snake : snakes) {
      if (snake[0] == position) {
        newPosition = snake[1];
        System.out.println("Player " + playerNumber + " hit a snake! Moving from " + position + " to " + newPosition);
        break;
      }
    }

    // Check for ladders
    for (int[] ladder : ladders) {
      if (ladder[0] == position) {
        newPosition = ladder[1];
        System.out.println("Player " + playerNumber + " found a ladder! Moving from " + position + " to " + newPosition);
        break;
      }
    }

    // If position changed due to snake or ladder, update after a short delay
    if (newPosition != position) {
      PauseTransition pause = new PauseTransition(Duration.millis(1000));
      int finalNewPosition = newPosition;
      pause.setOnFinished(e -> {
        // Update position label
        Label positionLabel = playerPositionLabels.get(playerNumber - 1);
        positionLabel.setText("at position: " + finalNewPosition);

        // Move the token to the new position
        movePlayerToken(playerNumber, finalNewPosition);
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
    row = BOARD_SIZE - 1 - row;

    // Calculate pixel coordinates (adding offset to center token in tile)
    int x = col * TILE_SIZE + TILE_SIZE / 2;
    int y = row * TILE_SIZE + TILE_SIZE / 2;

    return new int[] {x, y};
  }
}