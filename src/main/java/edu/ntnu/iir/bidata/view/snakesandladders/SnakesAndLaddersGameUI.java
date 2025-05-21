package edu.ntnu.iir.bidata.view.snakesandladders;

import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Observer;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.DiceView;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;

public class SnakesAndLaddersGameUI extends JavaFXGameUI {

  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersGameUI.class.getName());
  private final Map<String, ImageView> playerTokenMap = new HashMap<>();
  private final Map<String, Label> playerPositionLabels = new HashMap<>();
  private final int TILE_SIZE = 50;
  private int gridSize;
  private int boardSize;
  private DiceView localDiceView;
  private Pane playerLayer;
  private Button rollDiceBtn;
  private Button loadButton;
  private List<Player> playerNames;
  private boolean isLoadedGame = false;
  private String loadedGameName = null;
  private BorderPane root;
  private GameMediator mediator;
  private SnakesAndLaddersController controller;
  private StackPane boardPaneStack;
  private double boardImageOffsetX = 0;
  private double boardImageOffsetY = 0;
  private ImageView boardView;

  @Inject
  public SnakesAndLaddersGameUI(BoardGame boardGame, Stage primaryStage, SnakesAndLaddersController controller, List<Player> playerNames, GameMediator mediator) {
    super(boardGame, primaryStage);
    LOGGER.info("Initializing Snakes and Ladders Game UI with players: " + playerNames);
    this.playerNames = playerNames;
    this.mediator = mediator;
    this.controller = controller;
    if (this.mediator instanceof DefaultGameMediator m) {
      m.register((sender, event) -> {
        if ("nextPlayer".equals(event)) {
          javafx.application.Platform.runLater(() -> {
            updateCurrentPlayerIndicator(controller.getCurrentSnakesAndLaddersPlayerName());
          });
        }
      });
    }
    if (this.playerNames == null || this.playerNames.isEmpty()) {
      LOGGER.warning("No players provided, adding default player");
      this.playerNames = new ArrayList<>();
      this.playerNames.add(new Player("Player 1"));
    }
    setupUI();
    initializePlayerPositions();
  }

  /**
   * Updates the current player indicator in the UI
   */
  public void updateCurrentPlayerIndicator(String currentPlayer) {
    LOGGER.info("Updating current player indicator: " + currentPlayer);
    statusLabel.setText(currentPlayer + "'s Turn");
  }

  private void setUpTopBarWithControls() {
    Button backButton;
    Button saveButton = CommonButtons.saveGameBtn(false, controller, new Label());
    HBox topBar = new HBox(20);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);

    backButton = CommonButtons.backToMainMenu(primaryStage, false, controller);
    backButton.getStyleClass().add("game-control-button");

    topBar.getChildren().addAll(backButton, saveButton);
    root.setTop(topBar);
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

  @Override
  protected void setupBoardPane() {
    // Custom board setup for Snakes and Ladders
    boardPane.setPadding(new Insets(20));
    boardPane.setHgap(2);
    boardPane.setVgap(2);
    boardPane.setAlignment(Pos.CENTER);
    if (boardGame != null && boardGame.getBoard() != null) {
      this.boardSize = boardGame.getBoard().getSizeOfBoard();
      this.gridSize = (int) Math.ceil(Math.sqrt(boardSize));
    } else {
      this.boardSize = 100;
      this.gridSize = 10;
    }
    Image boardImage =
        new Image(
            Objects.requireNonNull(
                getClass().getResourceAsStream("/snakes_and_ladders_board.jpeg")));
    boardView = new ImageView(boardImage);
    boardView.setFitWidth(TILE_SIZE * gridSize);
    boardView.setFitHeight(TILE_SIZE * gridSize);
    boardView.setPreserveRatio(false);
    boardPaneStack = new StackPane();
    boardPaneStack.setAlignment(Pos.CENTER_LEFT);
    boardPaneStack.getChildren().add(boardView);
    playerLayer = new Pane();
    playerLayer.setPrefSize(boardView.getFitWidth(), boardView.getFitHeight());
    boardPaneStack.getChildren().add(playerLayer);
    boardPane.add(boardPaneStack, 0, 0);
  }

  @Override
  protected void setupUI() {
    super.setupUI();
    BorderPane root = (BorderPane) primaryStage.getScene().getRoot();

    // --- Top bar: Back and Save buttons ---
    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);
    Button backButton = CommonButtons.backToMainMenu(primaryStage, false, controller);
    backButton.getStyleClass().add("game-control-button");
    topBar.getChildren().addAll(backButton, saveButton);
    root.setTop(topBar);

    // --- Bottom bar: Dice view and Roll button ---
    localDiceView = new DiceView();
    rollDiceBtn = new Button("Roll Dice");
    rollDiceBtn.getStyleClass().add("game-control-button");
    rollDiceBtn.setOnAction(e -> rollDiceAndMove());
    HBox controls = new HBox(20);
    controls.setPadding(new Insets(20));
    controls.setAlignment(Pos.CENTER);
    controls.getChildren().addAll(localDiceView, rollDiceBtn, pauseButton);
    root.setBottom(controls);

    // --- Player info panel ---
    setupPlayerInfoPanelCustom();

    // Calculate board image offset after scene is shown
    primaryStage.getScene().getWindow().addEventHandler(javafx.stage.WindowEvent.WINDOW_SHOWN, e -> {
      javafx.geometry.Point2D offset = boardView.localToParent(0, 0);
      boardImageOffsetX = offset.getX();
      boardImageOffsetY = offset.getY();
      // After offset is known, update all tokens to correct positions
      for (Player player : playerNames) {
        updatePlayerPosition(player.getName());
      }
    });
  }

  private void setupPlayerInfoPanelCustom() {
    VBox playerPanel = playerInfoPane;
    playerPanel.getChildren().clear();
    playerPanel.setPadding(new Insets(20));
    playerPanel.getStyleClass().add("snl-player-panel");
    playerPanel.setPrefWidth(250);
    playerPanel.setAlignment(Pos.TOP_LEFT);

    // Add status label at the top of the player panel
    statusLabel.setText("Game Started!");
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
      int actualPosition = playerNames.get(i).getCurrentPosition();
      Label posLabel = new Label("at position: " + actualPosition);
      playerPositionLabels.put(playerName, posLabel);

      ImageView token = createPlayerToken(playerNames.get(i));
      playerTokenMap.put(playerName, token);

      HBox nameBox = new HBox(10);
      nameBox.setAlignment(Pos.CENTER_LEFT);
      nameBox.getChildren().addAll(token, playerLabel);

      playerBox.getChildren().addAll(nameBox, posLabel);
      playerPanel.getChildren().add(playerBox);
    }
  }

  /**
   * Creates a player token as an ImageView using the player's token image
   *
   * @param player the Player object
   * @return the ImageView representing the player token
   */
  private ImageView createPlayerToken(Player player) {
    String tokenImage = player.getTokenImage();
    ImageView tokenView;
    if (tokenImage != null && !tokenImage.isEmpty()) {
      Image img = new Image(getClass().getResourceAsStream("/tokens/" + tokenImage));
      tokenView = new ImageView(img);
      tokenView.setFitWidth(32);
      tokenView.setFitHeight(48);
    } else {
      tokenView = new ImageView();
      tokenView.setFitWidth(32);
      tokenView.setFitHeight(48);
    }
    playerLayer.getChildren().add(tokenView);
    // Place off-board at game start
    tokenView.setTranslateX(-TILE_SIZE);
    tokenView.setTranslateY(TILE_SIZE * gridSize);
    return tokenView;
  }

  /**
   * Initializes all player positions to the starting position
   */
  private void initializePlayerPositions() {
    LOGGER.info("Initializing player positions");
    for (Player player : playerNames) {
      int position = player.getCurrentPosition();
      movePlayerToken(player.getName(), position);
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

    controller.rollDice();
    int[] rolls = controller.getLastDiceRolls();
    int sum = controller.getLastDiceSum();
    localDiceView.setValues(rolls.length > 0 ? rolls[0] : 1, rolls.length > 1 ? rolls[1] : (rolls.length > 0 ? rolls[0] : 1));
    statusLabel.setText(currentPlayer + " rolled a " + (rolls.length > 0 ? rolls[0] : 1) + " and " + (rolls.length > 1 ? rolls[1] : (rolls.length > 0 ? rolls[0] : 1)) + "! (Total: " + sum + ")");
    PauseTransition pause = new PauseTransition(Duration.millis(800));
    pause.setOnFinished(
        event -> {
          SnakesAndLaddersController.MoveResult result = controller.movePlayer(currentPlayer, sum);

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
   * Display a message about a snake or ladder
   *
   * @param playerName the player's name
   * @param fromPosition the starting position
   * @param toPosition the ending position
   * @param type the type of snake or ladder
   */
  public void displaySnakeOrLadderMessage(
      String playerName, int fromPosition, int toPosition, String type) {
    statusLabel.setText(
        playerName + " hit a " + type + "! Moving from " + fromPosition + " to " + toPosition);

    // Add a short delay before actually moving the token
    PauseTransition pause = new PauseTransition(Duration.millis(1000));
    pause.setOnFinished(
        e -> {
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
  public void refreshUIFromBoardGame() {
    javafx.application.Platform.runLater(() -> {
      for (Player player : playerNames) {
        updatePlayerPosition(player.getName());
      }
      if (controller != null) {
        String currentPlayer = controller.getCurrentSnakesAndLaddersPlayerName();
        updateCurrentPlayerIndicator(currentPlayer);
      }
    });
  }

  @Override
  public Scene getScene() {
    return primaryStage.getScene();
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
   * @param position the board position (1-100)
   */
  private void movePlayerToken(String playerName, int position) {
    ImageView token = playerTokenMap.get(playerName);
    if (token == null) {
      return;
    }

    if (position == 0) {
      if (token.getParent() != playerLayer) {
        if (token.getParent() != null) {
          ((Pane) token.getParent()).getChildren().remove(token);
        }
        playerLayer.getChildren().add(token);
      }
      token.setTranslateX(-TILE_SIZE);
      token.setTranslateY(TILE_SIZE * gridSize);
      return;
    }

    if (position < 1 || position > boardSize) {
      return;
    }

    if (token.getParent() != playerLayer) {
      if (token.getParent() != null) {
        ((Pane) token.getParent()).getChildren().remove(token);
      }
      playerLayer.getChildren().add(token);
    }
    int[] coordinates = getCoordinatesForPosition(position);
    int playerIndex = playerNames.indexOf(playerName);
    int offsetX = playerIndex * 4 - 1;
    int offsetY = playerIndex * 6 - 5;
    token.setTranslateX(boardImageOffsetX + coordinates[0] + offsetX - 7);
    token.setTranslateY(boardImageOffsetY + coordinates[1] + offsetY - 68);
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

    return new int[] {x, y};
  }

  public void setBoardGame(BoardGame newBoardGame) {
    if (newBoardGame != null) {
      newBoardGame.addObserver(this);
    }
  }

  public BorderPane getRoot() {
    return root;
  }
}
