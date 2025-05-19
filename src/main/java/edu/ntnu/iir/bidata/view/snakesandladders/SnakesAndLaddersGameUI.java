package edu.ntnu.iir.bidata.view.snakesandladders;

import edu.ntnu.iir.bidata.controller.BaseGameController;
import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.model.Observer;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.view.common.BoardManagementUI;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.DiceView;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
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
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.io.File;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import edu.ntnu.iir.bidata.model.BoardGame;
import java.util.logging.Level;

public class SnakesAndLaddersGameUI implements Observer {

  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersGameUI.class.getName());
  private final Stage primaryStage;
  private final Map<String, Circle> playerTokenMap = new HashMap<>();
  private final Map<String, Label> playerPositionLabels = new HashMap<>();
  private final int TILE_SIZE = 50;
  private int gridSize;
  private int boardSize;
  private final Color[] PLAYER_COLORS = {
      Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE
  };
  private DiceView diceView;
  private Pane playerLayer;
  private VBox playerPanel;
  private Button rollDiceBtn;
  private Button backButton;
  private Button saveButton;
  private Button loadButton;
  private Label statusLabel;
  private List<Player> playerNames;
  private SnakesAndLaddersController controller;
  private BoardGame boardGame;
  private boolean isLoadedGame = false;
  private String loadedGameName = null;
  private BorderPane root;

  /**
   * Constructor that receives the selected players from the menu
   *
   * @param primaryStage The primary stage
   * @param playerNames  List of player names selected in the menu
   */
  public SnakesAndLaddersGameUI(Stage primaryStage, List<Player> playerNames) {
    LOGGER.info("Initializing Snakes and Ladders Game UI with players: " + playerNames);
    this.primaryStage = primaryStage;
    this.playerNames = playerNames;

    // If no players were passed, add a default player
    if (this.playerNames == null || this.playerNames.isEmpty()) {
      LOGGER.warning("No players provided, adding default player");
      this.playerNames = new ArrayList<>();
      this.playerNames.add(new Player("Player 1"));
    }

    setupGamePage();
    initializePlayerPositions();
  }

  /**
   * Sets the game controller
   *
   * @param controller The game controller
   */
  public void setController(SnakesAndLaddersController controller) {
    LOGGER.info("Setting game controller");
    this.controller = controller;
    controller.setPlayerNames(
        playerNames.stream().map(Player::getName).collect(Collectors.toList()));
    updateCurrentPlayerIndicator(controller.getCurrentSnakesAndLaddersPlayerName());

    // Create top bar with back button and game controls
    setUpTopBarWithControls();
  }

  /**
   * Sets whether this is a loaded game and its name
   *
   * @param isLoaded Whether this is a loaded game
   * @param gameName The name of the loaded game
   */
  public void setLoadedGame(boolean isLoaded, String gameName) {
    this.isLoadedGame = isLoaded;
    this.loadedGameName = gameName;
  }

  /**
   * Updates the current player indicator in the UI
   */
  public void updateCurrentPlayerIndicator(String currentPlayer) {
    LOGGER.info("Updating current player indicator: " + currentPlayer);
    statusLabel.setText(currentPlayer + "'s Turn");
  }

  private void handleSaveGame(){

    LOGGER.info("NEW FEAT: Saving game: " + "new-game");
    controller.saveGame("new-game", false);

  }
  private void setupGamePage() {
    LOGGER.info("Setting up game page");
    primaryStage.setTitle("Snakes & Ladders - Game");

    root = new BorderPane();
    root.setPadding(new Insets(20));
    root.getStyleClass().add("snl-game-root");

    // --- Board (center) ---
    StackPane boardPane = new StackPane();
    boardPane.setAlignment(Pos.CENTER_LEFT);

    if (boardGame != null && boardGame.getBoard() != null) {
      this.boardSize = boardGame.getBoard().getSizeOfBoard();
      this.gridSize = (int) Math.ceil(Math.sqrt(boardSize));
    } else {
      this.boardSize = 100;
      this.gridSize = 10;
    }

    // Load custom board image
    Image boardImage = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/snakes_and_ladders_board.jpeg")));
    ImageView boardView = new ImageView(boardImage);
    boardView.setFitWidth(TILE_SIZE * gridSize);
    boardView.setFitHeight(TILE_SIZE * gridSize);
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
    playerPanel.getStyleClass().add("snl-player-panel");
    playerPanel.setPrefWidth(250);
    playerPanel.setAlignment(Pos.TOP_LEFT);

    // Add status label at the top of the player panel
    statusLabel = new Label("Game Started!");
    statusLabel.getStyleClass().add("snl-game-status-label");
    statusLabel.setWrapText(true);

    playerPanel.getChildren().add(statusLabel);
    playerPanel.getChildren().add(new Label("--------------------"));

    // Create player tokens and labels based on selected players
    for (int i = 0; i < playerNames.size(); i++) {
      String playerName = playerNames.get(i).getName();

      // Create player info section
      VBox playerBox = new VBox(5);
      playerBox.getStyleClass().add("snl-player-info-box");

      Label playerLabel = new Label(playerName.toUpperCase() + ":");
      playerLabel.getStyleClass().add("snl-player-name-label");

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

    // --- Bottom: Roll dice and status ---
    VBox bottomBox = new VBox(10);
    bottomBox.setAlignment(Pos.CENTER);
    bottomBox.setPadding(new Insets(10));

    HBox diceBox = new HBox(20);
    diceBox.setAlignment(Pos.CENTER);

    rollDiceBtn = new Button("ROLL DICE");
    rollDiceBtn.getStyleClass().add("game-control-button");
    rollDiceBtn.setOnAction(e -> rollDiceAndMove());

    diceView = new DiceView();
    diceBox.getChildren().addAll(rollDiceBtn, diceView);

    bottomBox.getChildren().add(diceBox);
    root.setBottom(bottomBox);

    Scene scene = new Scene(root, 1200, 800);
    scene.getStylesheets().addAll(
        getClass().getResource("/styles.css").toExternalForm(),
        getClass().getResource("/snakesandladders.css").toExternalForm()
    );
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void setUpTopBarWithControls() {
    HBox topBar = new HBox(20);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);

    backButton = CommonButtons.backToMainMenu(primaryStage, false, controller);
    backButton.getStyleClass().add("game-control-button");

    saveButton = new Button("Save Game");
    saveButton.getStyleClass().add("game-control-button");
    saveButton.setOnAction(e -> handleSaveGame()); //TODO: Implement the save game logic in controller or use from base game controller

    topBar.getChildren().addAll(backButton, saveButton);
    root.setTop(topBar);
  }

  /**
   * Initialize all player positions to the starting position
   */
  private void initializePlayerPositions() {
    LOGGER.info("Initializing player positions");
    for (Player player : playerNames) {
      // Move token to starting position
      movePlayerToken(player.getName(), 0);
    }
  }

  /**
   * Roll the dice and move the current player
   */
  private void rollDiceAndMove() {
    if (controller == null) {
      return;
    }

    rollDiceBtn.setDisable(true);

    String currentPlayer = controller.getCurrentSnakesAndLaddersPlayerName();

    controller.rollDiceForSnakesAndLadders();
    int roll = controller.getLastDiceRoll();
    diceView.setValue(roll);

    statusLabel.setText(currentPlayer + " rolled a " + roll + "!");

    PauseTransition pause = new PauseTransition(Duration.millis(800));
    pause.setOnFinished(event -> {
      SnakesAndLaddersController.MoveResult result = controller.movePlayer(currentPlayer, roll);

      // Update the player position immediately after the move
      updatePlayerPosition(currentPlayer);

      if (result.type.equals("snake")) {
        displaySnakeOrLadderMessage(currentPlayer, result.start, result.end, "snake");
        // Update position again after snake
        updatePlayerPosition(currentPlayer);
      } else if (result.type.equals("ladder")) {
        displaySnakeOrLadderMessage(currentPlayer, result.start, result.end, "ladder");
        // Update position again after ladder
        updatePlayerPosition(currentPlayer);
      }

      if (result.end == 100) {
        statusLabel.setText("🏆 " + currentPlayer + " WINS! 🏆");
        rollDiceBtn.setDisable(true);
        return;
      }

      controller.nextSnakesAndLaddersPlayer();
      updateCurrentPlayerIndicator(controller.getCurrentSnakesAndLaddersPlayerName());
      rollDiceBtn.setDisable(false);
    });
    pause.play();
  }

  /**
   * Creates a player token with the specified color
   *
   * @param playerNumber the player number (1-based)
   * @param playerName   the player's name
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
   * Display a message about a snake or ladder
   */
  public void displaySnakeOrLadderMessage(String playerName, int fromPosition, int toPosition,
      String type) {
    statusLabel.setText(
        playerName + " hit a " + type + "! Moving from " + fromPosition + " to " + toPosition);

    // Add a short delay before actually moving the token
    PauseTransition pause = new PauseTransition(Duration.millis(1000));
    pause.setOnFinished(e -> {
      // Move the token to the new position
      movePlayerToken(playerName, toPosition);
      // Update the position label
      Label positionLabel = playerPositionLabels.get(playerName);
      if (positionLabel != null) {
        positionLabel.setText("at position: " + toPosition);
      }
    });
    pause.play();
  }

  @Override
  public void update() {
    // Instead of updateBoard, update all player positions here
    if (controller != null) {
      for (Player player : playerNames) {
        updatePlayerPosition(player.getName());
      }
      String currentPlayer = controller.getCurrentSnakesAndLaddersPlayerName();
      updateCurrentPlayerIndicator(currentPlayer);
      // Optionally, show winner if game is over (if you have such logic)
    }
  }

  /**
   * Updates the position of a player in the UI
   *
   * @param playerName the player's name
   */
  private void updatePlayerPosition(String playerName) {
    if (controller == null) {
      return;
    }

    // Get current position from controller
    int position = controller.getPlayerPosition(playerName);

    // Update position label
    Label positionLabel = playerPositionLabels.get(playerName);
    positionLabel.setText("at position: " + position);

    // Move the token on the board
    movePlayerToken(playerName, position);
  }

  /**
   * Moves a player token to a specific position on the board
   *
   * @param playerName the player's name
   * @param position   the board position (1-100)
   */
  private void movePlayerToken(String playerName, int position) {
    Circle token = playerTokenMap.get(playerName);
    if (token == null) {
      return;
    }

    if (position == 0) {
      // Starting position (off board)
      token.setTranslateX(20);
      token.setTranslateY(TILE_SIZE * gridSize + 20);
      return;
    }

    if (position < 1 || position > boardSize) {
      return;
    }

    // Calculate coordinates for the position
    int[] coordinates = getCoordinatesForPosition(position);

    // Add a small offset based on player index to prevent complete overlap
    int playerIndex = playerNames.indexOf(playerName);
    int offsetX = playerIndex * 4 - 1;
    int offsetY = playerIndex * 6 - 5;

    // Move the token - adjust Y coordinate to move down by 2mm
    // Assuming 1mm is approximately 3-4 pixels on screen
    int downwardAdjustment = 25;
    token.setTranslateX(coordinates[0] + offsetX);
    token.setTranslateY(coordinates[1] + offsetY + downwardAdjustment); // Add to move down
  }

  /**
   * Maps a board position (1-100) to pixel coordinates on the board image
   *
   * @param position the board position (1-100)
   * @return x, y coordinates for the position on the board
   */
  public int[] getCoordinatesForPosition(int position) {
    if (position < 1 || position > boardSize) {
      throw new IllegalArgumentException("Position must be between 1 and " + boardSize);
    }

    int row = (position - 1) / gridSize;
    int col;

    // Handle snake and ladder board row alternating direction
    if (row % 2 == 0) { // Even rows (0, 2, 4, ...) go left to right
      col = (position - 1) % gridSize;
    } else { // Odd rows go right to left
      col = gridSize - 1 - ((position - 1) % gridSize);
    }

    // Flip row because the board starts from the bottom
    row = gridSize - row;

    // Calculate pixel coordinates (adding offset to center token in tile)
    int x = col * TILE_SIZE + TILE_SIZE / 2;
    int y = row * TILE_SIZE + TILE_SIZE / 2;

    return new int[]{x, y};
  }

  public void setBoardGame(BoardGame newBoardGame) {
    if (this.boardGame != null) {
      this.boardGame.removeObserver(this);
    }
    this.boardGame = newBoardGame;
    this.boardGame.addObserver(this);
    refreshUIFromBoardGame();
  }

  public void refreshUIFromBoardGame() {
    // Update all player positions and labels from the loaded BoardGame
    if (controller == null || boardGame == null) return;
    for (Player player : boardGame.getPlayers()) {
      String playerName = player.getName();
      int position = controller.getPlayerPosition(playerName);
      // Update position label
      Label positionLabel = playerPositionLabels.get(playerName);
      if (positionLabel != null) {
        positionLabel.setText("at position: " + position);
      }
      // Move the token on the board
      movePlayerToken(playerName, position);
    }
    // Update current player indicator
    String currentPlayer = controller.getCurrentSnakesAndLaddersPlayerName();
    updateCurrentPlayerIndicator(currentPlayer);
  }

  public BorderPane getRoot() {
    return root;
  }


  private void handleBackToMainMenu() {
    if (isLoadedGame && loadedGameName != null) {
      // Auto-save loaded games
      try {
        //controller.saveGame(loadedGameName);
        JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error auto-saving game", e);
        statusLabel.setText("Error saving game: " + e.getMessage());
      }
    } else {
      // For new games, ask if user wants to save
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Save Game");
      dialog.setHeaderText("Would you like to save this game before returning to the main menu?");
      dialog.setContentText("Choose an option:");

      ButtonType saveButtonType = new ButtonType("Save Game");
      ButtonType dontSaveButton = new ButtonType("Don't Save");
      ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

      dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, dontSaveButton, cancelButton);

      dialog.showAndWait().ifPresent(response -> {
        if (response == saveButtonType) {
         // handleSaveGame();
          // Wait a bit for the save to complete before returning to main menu
          PauseTransition pause = new PauseTransition(Duration.millis(500));
          pause.setOnFinished(event -> {
            JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
          });
          pause.play();
        } else if (response == dontSaveButton) {
          JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
        }
        // If cancel, do nothing and stay on the game screen
      });
    }
  }
}