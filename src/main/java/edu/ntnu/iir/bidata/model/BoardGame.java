package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.actions.base.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.SwitchPositionAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

/**
 * A facade class that handles the main game logic and coordinates between different components.
 * This class manages the game state, players, dice, and board interactions.
 *
 * @author kaamyashinde
 * @version 1.0.0
 */
@Getter
public class BoardGame implements Observable {

  private final Board board;
  private final List<Player> players;
  private final Dice dice;
  private transient List<Observer> observers = new ArrayList<>();
  private int currentPlayerIndex;
  private boolean gameOver;
  private boolean gameInitialized;
  private int roundNumber = 1;
  private String level = "medium";

  /**
   * Constructor for the NewBoardGame class.
   *
   * @param board The game board
   * @param numberOfDice The number of dice to use in the game
   * @throws IllegalArgumentException if boardSize or numberOfDice is invalid
   */
  public BoardGame(Board board, int numberOfDice) {
    this(board, new Dice(numberOfDice));
  }

  /**
   * Constructor for BoardGame with injected Dice (for testing).
   *
   * @param board The game board
   * @param dice The Dice instance to use
   */
  public BoardGame(Board board, Dice dice) {
    this.board = board;
    this.players = new ArrayList<>();
    this.dice = dice;
    this.currentPlayerIndex = 0;
    this.gameOver = false;
    this.gameInitialized = false;
  }

  /**
   * Adds a player to the game.
   *
   * @param playerName The name of the player to add
   * @return true if the player was added successfully, false otherwise
   */
  public boolean addPlayer(String playerName) {
    if (playerName == null || playerName.trim().isEmpty()) {
      return false;
    }
    Player player = new Player(playerName);
    boolean added = players.add(player);
    if (added) {
      notifyObservers();
    }
    return added;
  }

  /**
   * Starts the game by ensuring all players are at the starting position.
   *
   * @throws GameException if the game cannot be started
   */
  public void startGame() {
    if (players.isEmpty()) {
      throw new GameException("Cannot start game with no players");
    }

    // Ensure board is properly initialized
    Tile startingTile = board.getStartingTile();
    if (startingTile == null) {
      throw new GameException("Board is not properly initialized - starting tile is null");
    }

    // Set all players to starting position
    players.forEach(player -> player.setCurrentTile(startingTile));

    currentPlayerIndex = 0;
    gameOver = false;
    gameInitialized = true;
    notifyObservers();
  }

  /**
   * Makes a move for the current player and returns detailed move info. This includes rolling the
   * dice, moving, and applying tile actions.
   *
   * @return MoveResult containing all move details
   * @throws GameException if the game is not properly initialized
   */
  public MoveResult makeMoveWithResult() {
    if (!gameInitialized) {
      throw new GameException("Game has not been started. Call startGame() first.");
    }
    if (gameOver) {
      return null;
    }
    if (currentPlayerIndex == 0) {
      roundNumber++;
    }
    Player currentPlayer = players.get(currentPlayerIndex);
    if (currentPlayer.getCurrentTile() == null) {
      throw new GameException("Current player's position is not set");
    }
    int prevPos = currentPlayer.getCurrentPosition();
    String playerName = currentPlayer.getName();
    int[] diceValues = null;
    int posAfterMove = prevPos;
    int posAfterAction = prevPos;
    String actionDesc = "";
    boolean skipTurn = false;
    if (currentPlayer.isSkipNextTurn()) {
      currentPlayer.setSkipNextTurn(false);
      skipTurn = true;
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      notifyObservers();
      return new MoveResult(playerName, prevPos, prevPos, prevPos, new int[0], "Skip Turn");
    }
    dice.rollAllDice();
    diceValues = dice.getLastRolledValues();
    int steps = dice.sumOfRolledValues();
    try {
      currentPlayer.move(steps);
      posAfterMove = currentPlayer.getCurrentPosition();
      Tile landedTile = currentPlayer.getCurrentTile();
      if (landedTile != null && landedTile.getAction() != null) {
        actionDesc = landedTile.getAction().getDescription();
        landedTile.getAction().executeAction(currentPlayer, landedTile);
        posAfterAction = currentPlayer.getCurrentPosition();
      } else {
        posAfterAction = posAfterMove;
      }
      if (currentPlayer.isOnLastTile()) {
        gameOver = true;
      }
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      notifyObservers();
      return new MoveResult(
          playerName, prevPos, posAfterMove, posAfterAction, diceValues, actionDesc);
    } catch (GameException e) {
      gameOver = true;
      notifyObservers();
      return new MoveResult(
          playerName,
          prevPos,
          posAfterMove,
          posAfterAction,
          diceValues,
          actionDesc + " (GameException: " + e.getMessage() + ")");
    }
  }

  /**
   * Gets the current player.
   *
   * @return The current player
   */
  public Player getCurrentPlayer() {
    return players.get(currentPlayerIndex);
  }

  /**
   * Gets the winner of the game.
   *
   * @return The winning player, or null if the game is not over
   */
  public Player getWinner() {
    if (!gameOver) {
      return null;
    }
    return players.get(currentPlayerIndex);
  }

  /**
   * Checks if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  public boolean isGameOver() {
    return gameOver;
  }

  /**
   * Gets the current dice values.
   *
   * @return An array of the current dice values
   */
  public int[] getCurrentDiceValues() {
    return dice.getLastRolledValues();
  }

  @Override
  public void addObserver(Observer observer) {
    if (observers == null) observers = new ArrayList<>();
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    if (observers == null) observers = new ArrayList<>();
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    if (observers == null) observers = new ArrayList<>();
    observers.forEach(Observer::update);
  }

  /**
   * Sets the players for the game.
   *
   * @param players The new list of players
   */
  public void setPlayers(List<Player> players) {
    this.players.clear();
    this.players.addAll(players);
    // Reset player positions to starting tile
    Tile startingTile = board.getStartingTile();
    this.players.forEach(player -> player.setCurrentTile(startingTile));
    notifyObservers();
  }

  public void setCurrentPlayerIndex(int index) {
    this.currentPlayerIndex = index;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        board, players, dice, currentPlayerIndex, gameOver, gameInitialized, roundNumber, level);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    BoardGame other = (BoardGame) obj;
    return board.equals(other.board)
        && players.equals(other.players)
        && dice.equals(other.dice)
        && currentPlayerIndex == other.currentPlayerIndex
        && gameOver == other.gameOver
        && gameInitialized == other.gameInitialized
        && roundNumber == other.roundNumber
        && level.equals(other.level);
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = (level == null ? "medium" : level);
  }

  /** Result object for a move, containing all relevant info for display. */
  public static class MoveResult {

    public final String playerName;
    public final int prevPos;
    public final int posAfterMove;
    public final int posAfterAction;
    public final int[] diceValues;
    public final String actionDesc;

    /**
     * Creates a result object holding detailed information about a player's move in the game.
     *
     * @param playerName The name of the player making the move
     * @param prevPos The player's position on the board before the move
     * @param posAfterMove The player's position on the board after the move, but before any actions
     *     are applied
     * @param posAfterAction The player's position on the board after any actions on the landed tile
     *     are applied
     * @param diceValues The values rolled on the dice for this move
     * @param actionDesc A description of the action carried out on the landed tile, if any
     */
    public MoveResult(
        String playerName,
        int prevPos,
        int posAfterMove,
        int posAfterAction,
        int[] diceValues,
        String actionDesc) {
      this.playerName = playerName;
      this.prevPos = prevPos;
      this.posAfterMove = posAfterMove;
      this.posAfterAction = posAfterAction;
      this.diceValues = diceValues;
      this.actionDesc = actionDesc;
    }
  }
}
