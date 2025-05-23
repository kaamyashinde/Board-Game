package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReader;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriter;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import java.util.List;
import java.util.logging.Logger;

/** Controller class specifically for Snakes and Ladders game logic. */
public class SnakesAndLaddersController extends BaseGameController {
  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersController.class.getName());

  private final BoardGameFileWriter boardGameWriter;
  private final BoardGameFileReader boardGameReader;
  private final GameMediator mediator;
  private final TileConfiguration tileConfig;
  private boolean gameStarted = false;
  private BoardGame boardGame;

  /**
   * Constructor for the SnakesAndLaddersController class, responsible for managing the game logic
   * of a Snakes and Ladders game. This controller initializes the required dependencies and
   * configurations.
   *
   * @param boardGame the BoardGame instance representing the game board and state
   * @param boardGameWriter the BoardGameFileWriter instance used for saving game state
   * @param boardGameReader the BoardGameFileReader instance used for loading game state
   * @param mediator the GameMediator instance handling communication and game events
   * @param tileConfig the TileConfiguration instance defining the board's tiles setup
   */
  @Inject
  public SnakesAndLaddersController(
      BoardGame boardGame,
      BoardGameFileWriter boardGameWriter,
      BoardGameFileReader boardGameReader,
      GameMediator mediator,
      TileConfiguration tileConfig) {
    super(boardGame);
    this.boardGame = boardGame;
    this.boardGameWriter = boardGameWriter;
    this.boardGameReader = boardGameReader;
    this.mediator = mediator;
    this.tileConfig = tileConfig;
    LOGGER.info("SnakesAndLaddersController initialized");
  }

  /**
   * Sets the player names for the Snakes and Ladders game and initializes their starting positions
   * on the board. Each player's current position is set to the first tile of the game board.
   *
   * @param playerNames a list of player names to be set in the game
   */
  @Override
  public void setPlayerNames(List<String> playerNames) {
    super.setPlayerNames(playerNames);
    // Initialize positions for all players
    playerNames.forEach(
        playerName ->
            boardGame.getPlayers().stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .ifPresent(player -> player.setCurrentTile(boardGame.getBoard().getTile(0))));
    LOGGER.info("Setting player names: " + playerNames);
  }

  /**
   * Starts the Snakes and Ladders game and updates the game state to indicate that the game has
   * begun. This method ensures that the game logic for initialization is properly handled and logs
   * the event.
   *
   * <p>The player positions do not require explicit initialization, as this is managed by the
   * BoardGame class. The method also logs the player names involved in the game at startup.
   *
   * <p>This method overrides the base implementation from the super class to provide game-specific
   * start logic for Snakes and Ladders.
   */
  @Override
  public void startGame() {
    super.startGame();
    gameStarted = true;
    // No need to initialize player positions; handled by BoardGame
    LOGGER.info("Snakes and Ladders game started with players: " + playerNames);
  }

  /**
   * Handles the current player's move in the Snakes and Ladders game.
   *
   * <p>This method checks whether the game has started before processing the move. If the game has
   * not started, a warning is logged, and the method exits without further action.
   *
   * <p>The current player's name is retrieved using the method {@code
   * getCurrentSnakesAndLaddersPlayerName()}, and their move is processed based on the result of the
   * last dice roll, obtained via {@code getLastDiceRoll()}. The move is executed with the use of
   * the {@code movePlayer} method, which calculates the new position of the player on the board
   * considering the roll value and potential interactions with snakes or ladders. If the player
   * reaches the end tile (position 100), a log entry declares the player as the winner, and the
   * method exits.
   *
   * <p>If the game is still ongoing, the method uses the {@code mediator.notify()} function to
   * trigger the turn for the next player.
   *
   * <p>This method overrides the default implementation in the superclass to handle Snakes and
   * Ladders-specific game logic.
   */
  @Override
  public void handlePlayerMove() {
    if (!gameStarted) {
      LOGGER.warning("Cannot handle player move: Game has not started");
      return;
    }

    String currentPlayer = getCurrentSnakesAndLaddersPlayerName();
    int roll = getLastDiceRoll();
    MoveResult result = movePlayer(currentPlayer, roll);

    if (result.end == 100) {
      LOGGER.info(currentPlayer + " has won the game!");
      return;
    }

    // Use mediator to notify next player
    mediator.notify(this, "nextPlayer");
    boardGame.notifyObservers(); // Notify observers after every move
  }

  /**
   * Retrieves the name of the current player in the Snakes and Ladders game.
   *
   * @return the name of the current player
   */
  public String getCurrentSnakesAndLaddersPlayerName() {
    return boardGame.getCurrentPlayer().getName();
  }

  /**
   * Retrieves the result of the last dice roll in the Snakes and Ladders game. The value represents
   * the first dice roll in an array of dice rolls. If no dice rolls are available, it returns 0 by
   * default.
   *
   * @return the value of the last dice roll, or 0 if no dice values are available
   */
  public int getLastDiceRoll() {
    int[] values = getLastDiceRolls();
    return (values != null && values.length > 0) ? values[0] : 0;
  }

  /**
   * Moves the specified player according to the roll of the dice and updates their position in the
   * Snakes and Ladders game. Handles interactions with snakes and ladders based on the player's new
   * position.
   *
   * <p>If the new position exceeds the board's final position (100), the player's position is
   * capped at 100. If the player lands on a snake or ladder, their position is adjusted
   * accordingly, and the move type reflects the interaction.
   *
   * @param playerName the name of the player who is making the move
   * @param roll the value rolled by the dice that determines the number of steps to move
   * @return a MoveResult object containing the starting position, ending position, and the type of
   *     move (e.g., "normal", "snake", "ladder")
   */
  public MoveResult movePlayer(String playerName, int roll) {
    for (Player player : boardGame.getPlayers()) {
      if (player.getName().equals(playerName)) {
        int start = player.getCurrentPosition();
        int end = start + roll;
        String type = "normal";

        // Ensure we don't go past the board size
        int lastTile = boardGame.getBoard().getSizeOfBoard();
        if (end == lastTile) {
          return new MoveResult(start, end - 1, "win");
        }
        if (end > lastTile) {
          int overshoot = end - lastTile;
          end = lastTile - overshoot;
        }

        if (end != start) {
          player.setCurrentTile(boardGame.getBoard().getTile(end));
        }

        // Check for snakes
        if (tileConfig.isSnakeHead(end)) {
          int tail = tileConfig.getSnakeTail(end);
          player.setCurrentTile(boardGame.getBoard().getTile(tail));
          end = tail;
          type = "snake";
        }
        // Check for ladders only if it's a normal move
        else if (tileConfig.isLadderStart(end)) {
          int top = tileConfig.getLadderEnd(end);
          player.setCurrentTile(boardGame.getBoard().getTile(top));
          end = top;
          type = "ladder";
        }

        return new MoveResult(start, end, type);
      }
    }
    return new MoveResult(0, 0, "normal");
  }

  /**
   * Retrieves an array representing the values of the dice from the last roll in the Snakes and
   * Ladders game.
   *
   * @return an array of integers containing the values of the last rolled dice
   */
  public int[] getLastDiceRolls() {
    return boardGame.getCurrentDiceValues();
  }

  /**
   * Calculates and returns the sum of the last rolled dice values. If no dice values are available,
   * the sum is considered to be 0.
   *
   * @return the sum of the values of the last rolled dice, or 0 if no dice values are available
   */
  public int getLastDiceSum() {
    int[] values = boardGame.getCurrentDiceValues();
    int sum = 0;
    if (values != null) {
      sum = java.util.Arrays.stream(values).sum();
    }
    return sum;
  }

  /**
   * Rolls all the dice in the Snakes and Ladders game and updates the game state to reflect that
   * the dice have been rolled. This method performs the following actions: - Invokes the {@code
   * rollAllDice} method on the dice associated with the game board to generate new rolled values
   * for all dice. - Updates the internal dice-rolled state to {@code true}. - Logs the current dice
   * values after the roll using the game's logger.
   */
  public void rollDice() {
    boardGame.getDice().rollAllDice();
    diceRolled = true;
    LOGGER.info("Dice rolled: " + java.util.Arrays.toString(boardGame.getCurrentDiceValues()));
  }

  /**
   * Advances the turn to the next player in the Snakes and Ladders game.
   *
   * <p>This method updates the current player index to the next player in the game's player list,
   * looping back to the first player if the end of the list is reached. It ensures that the turn
   * order is maintained cyclically. The method also logs the name of the next player whose turn it
   * is.
   *
   * <p>The current player index is set using the {@code setCurrentPlayerIndex} method, which
   * calculates the next player's index by incrementing the current index, and ensures the value
   * wraps around using the modulo operator with the player list size. The name of the next player
   * is logged using the {@code getCurrentSnakesAndLaddersPlayerName} method.
   */
  public void nextSnakesAndLaddersPlayer() {
    boardGame.setCurrentPlayerIndex(
        (boardGame.getCurrentPlayerIndex() + 1) % boardGame.getPlayers().size());
    LOGGER.info("Next player: " + getCurrentSnakesAndLaddersPlayerName());
  }

  /**
   * Retrieves the current position of the specified player on the game board. If the player is not
   * found, the method returns 0 by default.
   *
   * @param playerName the name of the player whose position is to be retrieved
   * @return the current position of the specified player, or 0 if the player is not found
   */
  public int getPlayerPosition(String playerName) {
    return boardGame.getPlayers().stream()
        .filter(player -> player.getName().equals(playerName))
        .map(Player::getCurrentPosition)
        .findFirst()
        .orElse(0);
  }

  /**
   * Loads a saved Snakes and Ladders game from the specified file path. This method utilizes the
   * {@code loadGame} method to read and initialize the game state from a file, ensuring that it is
   * prepared for continued play.
   *
   * @param savePath the file path to the saved game state to be loaded
   */
  public void loadSnakesAndLadderGame(String savePath) {
    this.boardGame = this.loadGame(savePath, false);
  }

  /**
   * Updates the position of a player in the Snakes and Ladders game. This method finds the
   * specified player by name and adjusts their position on the board. If the player is found, they
   * are moved to the specified position, and a log entry is created to indicate the movement.
   *
   * @param playerName the name of the player whose position will be updated
   * @param position the new position to which the player will be moved
   */
  public void updateSnakesAndLaddersPosition(String playerName, int position) {
    boardGame.getPlayers().stream()
        .filter(player -> player.getName().equals(playerName))
        .findFirst()
        .ifPresent(
            player -> {
              int steps = position - player.getCurrentPosition();
              player.move(steps);
              LOGGER.info(playerName + " moved to position " + position);
            });
  }

  /**
   * Represents the result of a player's move in the Snakes and Ladders game.
   *
   * <p>Instances of this class provide details about a move, including the starting and ending
   * positions of the player and the type of move that occurred. The type of move indicates whether
   * it was a normal move, involved a snake, or involved a ladder.
   *
   * <p>This class is immutable and designed to encapsulate the details of a single move within the
   * game.
   */
  public static class MoveResult {
    public final int start;
    public final int end;
    public final String type; // "normal", "snake", "ladder"

    /**
     * Constructs a new MoveResult object representing the result of a move.
     *
     * @param start the starting position of the move
     * @param end the ending position of the move
     * @param type the type of the move (e.g., "normal", "snake", "ladder")
     */
    public MoveResult(int start, int end, String type) {
      this.start = start;
      this.end = end;
      this.type = type;
    }
  }
}
