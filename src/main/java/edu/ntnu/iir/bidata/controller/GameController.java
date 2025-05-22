package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/** Controls the flow of the game and coordinates between the model and view. */
public class GameController {

  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
  private final BoardGame boardGame;
  // Snakes and Ladders specific data
  private final int[][] snakes = {{99, 41}, {95, 75}, {89, 86}, {78, 15}, {38, 2}, {29, 11}};
  private final int[][] ladders = {
    {3, 36}, {8, 12}, {14, 26}, {31, 73}, {59, 80}, {83, 97}, {90, 92}
  };
  // For Snakes and Ladders specific logic
  private final Map<String, Integer> playerPositions = new HashMap<>();
  // For Ludo specific logic
  private final int diceValue = 1;
  private boolean gameStarted = false;
  @Setter @Getter private int currentPlayerIndex = 0;
  private List<String> playerNames;
  @Setter @Getter private boolean diceRolled = false;
  @Getter @Setter private boolean movingPiece = false;
  private boolean isPaused = false;

  /**
   * Constructs a new GameController that manages the lifecycle and interactions of a board game.
   *
   * @param boardGame the BoardGame instance to be managed by the GameController
   */
  public GameController(BoardGame boardGame) {
    this.boardGame = boardGame;
    LOGGER.info("GameController initialized");
  }

  /** Sets the player names for games that manage their own player list. */
  public void setPlayerNames(List<String> playerNames) {
    this.playerNames = playerNames;
    // Initialize positions for all players
    playerNames.forEach(playerName -> playerPositions.put(playerName, 0));
    LOGGER.info("Setting player names: " + playerNames);
  }

  /**
   * Starts the game by initializing the first turn if the game has not already started. This method
   * ensures the game state is updated to indicate the start of gameplay and logs the appropriate
   * messages.
   *
   * <p>The method checks the `gameStarted` flag to determine if the game has already begun. If not,
   * it sets `gameStarted` to true and logs the initiation of the first turn.
   *
   * <p>Preconditions: - `gameStarted` should be false for the game to start.
   *
   * <p>Postconditions: - Sets `gameStarted` to true if the game is started successfully. - Logs the
   * game start and first turn initiation to the application logger.
   *
   * <p>This method does not handle player turns or game mechanics; its sole responsibility is to
   * start the game if it is not already started.
   */
  public void startGame() {
    LOGGER.info("Starting new game");
    if (!gameStarted) {
      // Start the first turn
      gameStarted = true;
      LOGGER.info("First turn started");
    }
  }

  /**
   * Rolls dice for Snakes and Ladders game.
   *
   * @return the dice roll value
   */
  public int rollDiceForSnakesAndLadders() {
    return 1 + (int) (Math.random() * 6);
  }

  /**
   * Updates player position for Snakes and Ladders game.
   *
   * @param playerName the player's name
   * @param diceRoll the dice roll value
   * @return true if the player won, false otherwise
   */
  public boolean updateSnakesAndLaddersPosition(String playerName, int diceRoll) {
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

    // Check for snakes and ladders
    int finalPosition = checkSnakesAndLadders(playerName, newPosition);
    if (finalPosition != newPosition) {
      playerPositions.put(playerName, finalPosition);
    }

    // Check for win condition
    return playerPositions.get(playerName) == 100;
  }

  /**
   * Checks if a position has a snake or ladder.
   *
   * @param playerName the player's name
   * @param position the current position
   * @return the new position after snake or ladder
   */
  private int checkSnakesAndLadders(String playerName, int position) {
    int newPosition = position;

    // Check for snakes
    newPosition =
        java.util.Arrays.stream(snakes)
            .filter(snake -> snake[0] == position)
            .map(snake -> snake[1])
            .findFirst()
            .orElse(newPosition);

    // Check for ladders
    newPosition =
        java.util.Arrays.stream(ladders)
            .filter(ladder -> ladder[0] == position)
            .map(ladder -> ladder[1])
            .findFirst()
            .orElse(newPosition);

    return newPosition;
  }

  /** Moves to the next player in Snakes and Ladders. */
  public void nextSnakesAndLaddersPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
  }

  /**
   * Gets the current player's name for Snakes and Ladders.
   *
   * @return the current player's name
   */
  public String getCurrentSnakesAndLaddersPlayerName() {
    String name = playerNames.get(currentPlayerIndex);
    LOGGER.info("Current Snakes and Ladders player: " + name);
    return name;
  }

  /**
   * Gets the position for a player in Snakes and Ladders.
   *
   * @param playerName the player's name
   * @return the player's position
   */
  public int getPlayerPosition(String playerName) {
    return playerPositions.get(playerName);
  }

  /* Ludo Game Specific Methods */

  /**
   * Rolls dice for Ludo game.
   *
   * @return the dice roll value
   */
  public int rollDiceForLudo() {
    diceRolled = true;
    return 1 + (int) (Math.random() * 6);
  }

  /**
   * Checks if a token can be moved in Ludo.
   *
   * @param currentPosition the token's current position
   * @param diceValue the dice roll value
   * @return true if the token can be moved, false otherwise
   */
  public boolean canMoveLudoToken(int currentPosition, int diceValue) {
    // If in home and rolled a 6, can move out
    if (currentPosition == -1 && diceValue == 6) {
      return true;
    }

    // If already on the board, can move
    return currentPosition >= 0;
  }

  /**
   * Checks whether the game is over. If the game is over, logs the winner's name.
   *
   * @return true if the game has concluded, false otherwise
   */
  public boolean isGameOver() {
    boolean gameOver = boardGame.isGameOver();
    if (gameOver) {
      LOGGER.info("Game over. Winner: " + boardGame.getWinner().getName());
    }
    return gameOver;
  }

  /**
   * Retrieves the current player in the game and logs their name.
   *
   * @return the current player of the game
   */
  public Player getCurrentPlayer() {
    Player player = boardGame.getCurrentPlayer();
    LOGGER.info("Current player: " + player.getName());
    return player;
  }

  /**
   * Executes the current player's move in the board game and returns the result of the move. The
   * move may involve updating player positions, performing actions, or interacting with game
   * elements such as snakes, ladders, or tiles.
   *
   * <p>This method logs the details of the move, including the player's name, previous position,
   * new position after the move, any position changes due to subsequent actions, and a description
   * of the action performed.
   *
   * @return the result of the move as a {@code BoardGame.MoveResult} object containing details
   *     about the player's move, or {@code null} if the move could not be completed.
   */
  public BoardGame.MoveResult makeMove() {
    LOGGER.info("Making move for current player");
    BoardGame.MoveResult result = boardGame.makeMoveWithResult();
    if (result != null) {
      LOGGER.info(
          String.format(
              "Player %s moved from %d to %d (after action: %d). Action: %s",
              result.playerName,
              result.prevPos,
              result.posAfterMove,
              result.posAfterAction,
              result.actionDesc));
    }
    return result;
  }

  /**
   * Handles the action performed on a tile during the game. This method processes the specified
   * {@code TileAction}, executing its associated behavior within the game's context. This may
   * involve updates to the player state, interactions with the game board, or other game-specific
   * logic triggered by the tile action.
   *
   * @param action the {@code TileAction} to be executed, which defines the specific behavior and
   *     effects associated with the tile
   */
  public void handleTileAction(TileAction action) {
    LOGGER.info("Handling tile action: " + action);
  }

  /**
   * Pauses the game by setting the paused state to true. This method updates the game state to
   * indicate that the game is currently not active and logs the action, ensuring that the game
   * remains suspended until resumed.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>The game should be running or already started.
   * </ul>
   *
   * <p>Postconditions:
   *
   * <ul>
   *   <li>The game state is set to paused.
   *   <li>An appropriate log entry is created indicating the game has been paused.
   * </ul>
   */
  public void pauseGame() {
    isPaused = true;
    LOGGER.info("Game paused");
  }

  /**
   * Resumes the game if it is currently paused. This method updates the game's state to indicate
   * that it is active and ready to continue. It logs the action of resuming the game.
   *
   * <p>Preconditions:
   *
   * <ul>
   *   <li>The game must be in a paused state before invoking this method.
   * </ul>
   *
   * <p>Postconditions: -
   *
   * <ul>
   *   <li>The game state is updated to indicate it is no longer paused.
   *   <li>A log entry is created documenting that the game has been resumed.
   * </ul>
   */
  public void resumeGame() {
    isPaused = false;
    LOGGER.info("Game resumed");
  }
}
