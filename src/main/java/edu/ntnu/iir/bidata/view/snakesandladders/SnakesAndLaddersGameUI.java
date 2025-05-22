package edu.ntnu.iir.bidata.view.snakesandladders;

import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.DiceView;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The SnakesAndLaddersGameUI class manages the user interface for a Snakes and Ladders game. This
 * class extends the JavaFXGameUI and is responsible for rendering the game board, handling player
 * interactions, and updating the UI based on game state changes. It supports features such as token
 * movement, dice rolls, and displaying messages for snakes and ladders.
 */
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
  private String boardImagePath;
  private String currentPlayer;

  /**
   * Constructs the SnakesAndLaddersGameUI, initializes the game UI components and connects the view
   * to the game logic.
   *
   * @param boardGame the BoardGame object representing the current game state
   * @param primaryStage the primary JavaFX Stage that displays the game UI
   * @param controller the SnakesAndLaddersController to handle user interactions and game logic
   * @param playerNames a list of players participating in this game
   * @param mediator the GameMediator to handle communication between components
   */
  @Inject
  public SnakesAndLaddersGameUI(
      BoardGame boardGame,
      Stage primaryStage,
      SnakesAndLaddersController controller,
      List<Player> playerNames,
      GameMediator mediator) {
    this(
        boardGame,
        primaryStage,
        controller,
        playerNames,
        mediator,
        "/snakeandladder_boardgame/snakes_and_ladders_board.jpeg");
  }

  /**
   * Constructs the SnakesAndLaddersGameUI, initializes the game UI components, and connects the
   * view to the game logic.
   *
   * @param boardGame the BoardGame object representing the current game state
   * @param primaryStage the primary JavaFX Stage that displays the game UI
   * @param controller the SnakesAndLaddersController to handle user interactions and game logic
   * @param playerNames a list of players participating in this game
   * @param mediator the GameMediator to handle communication between components
   * @param boardImagePath the file path to the board image used in the UI
   */
  @Inject
  public SnakesAndLaddersGameUI(
      BoardGame boardGame,
      Stage primaryStage,
      SnakesAndLaddersController controller,
      List<Player> playerNames,
      GameMediator mediator,
      String boardImagePath) {
    super(boardGame, primaryStage);
    LOGGER.info("Initializing Snakes and Ladders Game UI with players: " + playerNames);
    this.playerNames = playerNames;
    this.mediator = mediator;
    this.controller = controller;
    this.boardImagePath = boardImagePath;
    if (this.mediator instanceof DefaultGameMediator m) {
      m.register(
          (sender, event) -> {
            if ("nextPlayer".equals(event)) {
              javafx.application.Platform.runLater(
                  () -> {
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
    primaryStage.setMinWidth(870);
    primaryStage.setMinHeight(750);
    initializePlayerPositions();
  }

  /**
   * Sets whether this is a loaded game and its name.
   *
   * @param isLoaded Whether this is a loaded game
   * @param gameName The name of the loaded game
   */
  public void setLoadedGame(boolean isLoaded, String gameName) {
    this.isLoadedGame = isLoaded;
    this.loadedGameName = gameName;
  }

  /**
   * Configures the user interface for the Snakes and Ladders game.
   *
   * <p>This method sets up the layout of the game, including the top bar with navigation buttons,
   * the bottom bar for dice rolling and controls, and the player information panel. Additionally,
   * it calculates the offset of the game board image once the scene is displayed to correctly
   * position player tokens.
   *
   * <p>Key components and functionalities added: - A top bar with buttons for navigating back to
   * the main menu and saving the game. - A bottom bar with a dice view, a roll dice button for
   * moving players, and a pause button. - A custom player information panel. - Updates player token
   * positions based on the board image offset once the scene is shown.
   *
   * <p>The method overrides the base implementation to add specific elements necessary for the
   * Snakes and Ladders game while retaining inherited features.
   */
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
    primaryStage
        .getScene()
        .getWindow()
        .addEventHandler(
            javafx.stage.WindowEvent.WINDOW_SHOWN,
            e -> {
              javafx.geometry.Point2D offset = boardView.localToParent(0, 0);
              boardImageOffsetX = offset.getX();
              boardImageOffsetY = offset.getY();
              // After offset is known, update all tokens to correct positions
              playerNames.forEach(player -> updatePlayerPosition(player.getName()));
            });
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
        new Image(Objects.requireNonNull(getClass().getResourceAsStream(boardImagePath)));
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

  /**
   * Retrieves the Scene associated with the primary stage of the Snakes and Ladders game UI.
   *
   * @return the JavaFX Scene that is currently set on the primary stage
   */
  @Override
  public Scene getScene() {
    return primaryStage.getScene();
  }

  /**
   * Sets up the player information panel with customized styles and dynamic content.
   *
   * <p>This method initializes and populates the player information panel in the game UI by
   * clearing its current contents, applying styles, and adding details for each player, including
   * their name, position, and token. The panel is styled with a consistent theme and aligns its
   * content for optimal display.
   */
  private void setupPlayerInfoPanelCustom() {
    VBox playerPanel = playerInfoPane;
    playerPanel.getChildren().clear();
    playerPanel.setPadding(new Insets(20));
    playerPanel.getStyleClass().add("snl-player-panel");
    playerPanel.setPrefWidth(250);
    playerPanel.setAlignment(Pos.TOP_LEFT);
    statusLabel.setText("Game Started!");
    statusLabel.getStyleClass().add("snl-game-status-label");
    statusLabel.setWrapText(true);
    playerPanel.getChildren().add(statusLabel);
    playerPanel.getChildren().add(new Label("--------------------"));
    java.util.stream.IntStream.range(0, playerNames.size())
        .forEach(
            i -> {
              String playerName = playerNames.get(i).getName();
              VBox playerBox = new VBox(5);
              playerBox.getStyleClass().add("snl-player-info-box");
              Label playerLabel = new Label(playerName.toUpperCase() + ":");
              playerLabel.getStyleClass().add("snl-player-name-label");
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
            });
  }

  /**
   * Creates a player token as an ImageView using the player's token image.
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

  /** Initializes all player positions to the starting position. */
  private void initializePlayerPositions() {
    LOGGER.info("Initializing player positions");
    playerNames.forEach(
        player -> {
          int position = player.getCurrentPosition();
          movePlayerToken(player.getName(), position);
        });
  }

  /** Roll the dice and move the current player. */
  private void rollDiceAndMove() {
    try {
      if (controller == null) {
        return;
      }

      rollDiceBtn.setDisable(true);

      currentPlayer = controller.getCurrentSnakesAndLaddersPlayerName();

      controller.rollDice();
      int[] rolls = controller.getLastDiceRolls();
      int sum = controller.getLastDiceSum();
      localDiceView.setValues(
          rolls.length > 0 ? rolls[0] : 1,
          rolls.length > 1 ? rolls[1] : (rolls.length > 0 ? rolls[0] : 1));
      statusLabel.setText(
          currentPlayer
              + " rolled a "
              + (rolls.length > 0 ? rolls[0] : 1)
              + " and "
              + (rolls.length > 1 ? rolls[1] : (rolls.length > 0 ? rolls[0] : 1))
              + "! (Total: "
              + sum
              + ")");
      PauseTransition pause = new PauseTransition(Duration.millis(800));
      pause.setOnFinished(
          event -> {
            SnakesAndLaddersController.MoveResult result =
                controller.movePlayer(currentPlayer, sum);

            // Update the player position immediately after the move
            updatePlayerPosition(currentPlayer);

            if (result.type.equals("snake")) {
              LOGGER.info(
                  currentPlayer
                      + " hit a snake! Moving from "
                      + result.start
                      + " to "
                      + result.end);
              displaySnakeOrLadderMessage(currentPlayer, result.start, result.end, "snake");
              // Update position again after snake
              updatePlayerPosition(currentPlayer);
            } else if (result.type.equals("ladder")) {
              LOGGER.info(
                  currentPlayer
                      + " hit a ladder! Moving from "
                      + result.start
                      + " to "
                      + result.end);
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
    } catch (GameException e) {
      LOGGER.info("🏆 " + currentPlayer + " WINS! 🏆");
      statusLabel.setText("🏆 " + currentPlayer + " WINS! 🏆");
    }
  }

  /**
   * Display a message about a snake or ladder.
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

  /**
   * Updates the position of a player in the UI.
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

  /** Updates the current player indicator in the UI. */
  public void updateCurrentPlayerIndicator(String currentPlayer) {
    LOGGER.info("Updating current player indicator: " + currentPlayer);
    statusLabel.setText(currentPlayer + "'s Turn");
  }

  /**
   * Moves a player token to a specific position on the board.
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
      token.setTranslateX(-TILE_SIZE + 20);
      token.setTranslateY((TILE_SIZE * gridSize) - 50);
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
   * Maps a board position (1-100) to pixel coordinates on the board image.
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
    int displayRow = gridSize - row;
    int x = col * TILE_SIZE + TILE_SIZE / 2;
    int y = displayRow * TILE_SIZE + TILE_SIZE / 2;

    LOGGER.info(
        String.format(
            "Tile %d -> (row=%d, col=%d) -> (x=%d, y=%d)", position, displayRow, col, x, y));

    return new int[] {x, y};
  }

  /**
   * Sets the current board game for the UI and adds this UI as an observer of the game.
   *
   * @param newBoardGame the BoardGame object to set as the current board game
   */
  public void setBoardGame(BoardGame newBoardGame) {
    if (newBoardGame != null) {
      newBoardGame.addObserver(this);
    }
  }

  /**
   * Retrieves the root BorderPane of the game UI. The root pane serves as the main container for
   * all UI components in the Snakes and Ladders game.
   *
   * @return the root BorderPane of the game UI
   */
  public BorderPane getRoot() {
    return root;
  }
}
