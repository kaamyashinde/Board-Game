package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.game.GameState;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.Observable;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileWriter;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileReader;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileWriterGson;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileReaderGson;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 * Controls the flow of the game and coordinates between the model and view.
 */
public class GameController {

  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
  private final BoardGame boardGame;
  // Snakes and Ladders specific data
  private final int[][] snakes = {
      {99, 41}, {95, 75}, {89, 86}, {78, 15}, {38, 2}, {29, 11}
  };
  private final int[][] ladders = {
      {3, 36}, {8, 12}, {14, 26}, {31, 73}, {59, 80}, {83, 97}, {90, 92}
  };
  // For Snakes and Ladders specific logic
  private final Map<String, Integer> playerPositions = new HashMap<>();
  // For Ludo specific logic
  private final int diceValue = 1;
  private boolean gameStarted = false;
  @Setter
  @Getter
  private int currentPlayerIndex = 0;
  private List<String> playerNames;
  @Setter
  @Getter
  private boolean diceRolled = false;
  @Getter
  @Setter
  private boolean movingPiece = false;
  private final GameStateFileWriter gameStateWriter;
  private final GameStateFileReader gameStateReader;
  private boolean isPaused = false;

  public GameController(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.gameStateWriter = new GameStateFileWriterGson();
    this.gameStateReader = new GameStateFileReaderGson();
    LOGGER.info("GameController initialized");
  }

  /**
   * Sets the player names for games that manage their own player list
   */
  public void setPlayerNames(List<String> playerNames) {
    this.playerNames = playerNames;
    // Initialize positions for all players
    for (String playerName : playerNames) {
      playerPositions.put(playerName, 0);
    }
    LOGGER.info("Setting player names: " + playerNames);
  }

  public void startGame() {
    LOGGER.info("Starting new game");
    if (!gameStarted) {
      // Start the first turn
      gameStarted = true;
      LOGGER.info("First turn started");
    }
  }

  /**
   * Handles the dice roll and player movement for standard board games
   */
  public void handleNextTurn() {
    if (!boardGame.isGameOver()) {
      Player currentPlayer = boardGame.getCurrentPlayer();

      // Roll dice
      int diceRoll = rollDiceForSnakesAndLadders();

      // Move player
      boolean hasWon = updateSnakesAndLaddersPosition(currentPlayer.getName(), diceRoll);

      // Check if game is over
      if (hasWon) {
        // Handle win condition
      } else {
        // Move to next player
        nextSnakesAndLaddersPlayer();
        startNextTurn();
      }
    }
  }

  /**
   * Rolls dice for Snakes and Ladders game
   *
   * @return the dice roll value
   */
  public int rollDiceForSnakesAndLadders() {
    return 1 + (int) (Math.random() * 6);
  }

  /**
   * Updates player position for Snakes and Ladders game
   *
   * @param playerName the player's name
   * @param diceRoll   the dice roll value
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
   * Moves to the next player in Snakes and Ladders
   */
  public void nextSnakesAndLaddersPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
  }

  private void startNextTurn() {
    if (!boardGame.isGameOver()) {
      Player currentPlayer = boardGame.getCurrentPlayer();
      // Handle turn logic here
    } else if (boardGame instanceof BoardGame) {
      // For Snakes and Ladders specific turn management
      String currentPlayer = playerNames.get(currentPlayerIndex);
      // Handle turn logic here
    }
  }

  /**
   * Checks if a position has a snake or ladder
   *
   * @param playerName the player's name
   * @param position   the current position
   * @return the new position after snake or ladder
   */
  private int checkSnakesAndLadders(String playerName, int position) {
    int newPosition = position;

    // Check for snakes
    for (int[] snake : snakes) {
      if (snake[0] == position) {
        newPosition = snake[1];
        break;
      }
    }

    // Check for ladders
    for (int[] ladder : ladders) {
      if (ladder[0] == position) {
        newPosition = ladder[1];
        break;
      }
    }

    return newPosition;
  }

  /**
   * Gets the current player's name for Snakes and Ladders
   *
   * @return the current player's name
   */
  public String getCurrentSnakesAndLaddersPlayerName() {
    String name = playerNames.get(currentPlayerIndex);
    LOGGER.info("Current Snakes and Ladders player: " + name);
    return name;
  }

  /**
   * Gets the position for a player in Snakes and Ladders
   *
   * @param playerName the player's name
   * @return the player's position
   */
  public int getPlayerPosition(String playerName) {
    return playerPositions.get(playerName);
  }

  /* Ludo Game Specific Methods */

  /**
   * Rolls dice for Ludo game
   *
   * @return the dice roll value
   */
  public int rollDiceForLudo() {
    diceRolled = true;
    return 1 + (int) (Math.random() * 6);
  }

  /**
   * Checks if a token can be moved in Ludo
   *
   * @param currentPosition the token's current position
   * @param diceValue       the dice roll value
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

  public void rollDice() {
    LOGGER.info("Rolling dice");
    BoardGame.MoveResult result = boardGame.makeMoveWithResult();
    if (result != null) {
      LOGGER.info("Dice rolled: " + result.diceValues);
    }
  }

  public void movePlayer() {
    Player currentPlayer = boardGame.getCurrentPlayer();
    int oldPosition = currentPlayer.getCurrentPosition();
    BoardGame.MoveResult result = boardGame.makeMoveWithResult();
    if (result != null) {
      LOGGER.info(String.format("Player %s moved from position %d to %d",
          currentPlayer.getName(), oldPosition, result.posAfterMove));
    }
  }

  public boolean isGameOver() {
    boolean gameOver = boardGame.isGameOver();
    if (gameOver) {
      LOGGER.info("Game over. Winner: " + boardGame.getWinner().getName());
    }
    return gameOver;
  }

  public Player getCurrentPlayer() {
    Player player = boardGame.getCurrentPlayer();
    LOGGER.info("Current player: " + player.getName());
    return player;
  }

  public BoardGame.MoveResult makeMove() {
    LOGGER.info("Making move for current player");
    BoardGame.MoveResult result = boardGame.makeMoveWithResult();
    if (result != null) {
      LOGGER.info(String.format("Player %s moved from %d to %d (after action: %d). Action: %s",
          result.playerName, result.prevPos, result.posAfterMove, result.posAfterAction,
          result.actionDesc));
    }
    return result;
  }

  public void handleTileAction(TileAction action) {
    LOGGER.info("Handling tile action: " + action);
    // Handle the tile action
  }

  public void pauseGame() {
    isPaused = true;
    LOGGER.info("Game paused");
  }

  public void resumeGame() {
    isPaused = false;
    LOGGER.info("Game resumed");
  }

  public boolean isPaused() {
    return isPaused;
  }

  public void saveGame(String gameName) {
    if (!gameStarted) {
      LOGGER.warning("Cannot save game: Game has not started");
      return;
    }
    
    GameState gameState = new GameState(
        boardGame.getBoard(),
        boardGame.getPlayers(),
        currentPlayerIndex,
        gameName
    );
    
    Path savePath = Paths.get("src/main/resources/saved_games", gameName + ".json");
    gameStateWriter.writeGameState(gameState, savePath);
    LOGGER.info("Game saved: " + gameName);
  }

  public void loadGame(String gameName) {
    Path savePath = Paths.get("src/main/resources/saved_games", gameName + ".json");
    GameState gameState = gameStateReader.readGameState(savePath);
    
    // Create a new BoardGame instance with the loaded board
    BoardGame newBoardGame = new BoardGame(gameState.getBoard(), boardGame.getDice().getLastRolledValues().length);
    
    // Add all players from the saved state
    for (Player player : gameState.getPlayers()) {
        newBoardGame.addPlayer(player.getName());
    }
    
    // Set the current player index
    currentPlayerIndex = gameState.getCurrentPlayerIndex();
    gameStarted = true;
    
    // Initialize the new game
    newBoardGame.startGame();
    
    // Update the game state in the UI
    if (boardGame instanceof Observable) {
        ((Observable) boardGame).notifyObservers();
    }
    
    LOGGER.info("Game loaded: " + gameName);
  }
}