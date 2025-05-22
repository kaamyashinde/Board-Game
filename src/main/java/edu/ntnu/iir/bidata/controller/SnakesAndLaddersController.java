package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReader;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriter;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersGameUI;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import edu.ntnu.iir.bidata.Inject;

/** Controller class specifically for Snakes and Ladders game logic. */
public class SnakesAndLaddersController extends BaseGameController {
  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersController.class.getName());

  // Snakes and Ladders specific data
  private final int[][] snakes = {{99, 41}, {95, 75}, {89, 86}, {78, 15}, {38, 2}, {29, 11}};
  private final int[][] ladders = {
    {3, 36}, {8, 12}, {14, 26}, {31, 73}, {59, 80}, {83, 97}, {90, 92}
  };
  private final BoardGameFileWriter boardGameWriter;
  private final BoardGameFileReader boardGameReader;
  private boolean gameStarted = false;
  private BoardGame boardGame;
  private final GameMediator mediator;

  @Inject
  public SnakesAndLaddersController(BoardGame boardGame, BoardGameFileWriter boardGameWriter, BoardGameFileReader boardGameReader, GameMediator mediator) {
    super(boardGame);
    this.boardGame = boardGame;
    this.boardGameWriter = boardGameWriter;
    this.boardGameReader = boardGameReader;
    this.mediator = mediator;
    LOGGER.info("SnakesAndLaddersController initialized");
  }

  @Override
  public void setPlayerNames(List<String> playerNames) {
    super.setPlayerNames(playerNames);
    // Initialize positions for all players
    for (String playerName : playerNames) {
      for (Player player : boardGame.getPlayers()) {
        if (player.getName().equals(playerName)) {
          player.setCurrentTile(boardGame.getBoard().getTile(0));
          break;
        }
      }
    }
    LOGGER.info("Setting player names: " + playerNames);
  }

  @Override
  public void startGame() {
    super.startGame();
    gameStarted = true;
    // No need to initialize player positions; handled by BoardGame
    LOGGER.info("Snakes and Ladders game started with players: " + playerNames);
  }

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
  }

  public String getCurrentSnakesAndLaddersPlayerName() {
    return boardGame.getCurrentPlayer().getName();
  }

  public int[] getLastDiceRolls() {
    return boardGame.getCurrentDiceValues();
  }

  public int getLastDiceSum() {
    int[] values = boardGame.getCurrentDiceValues();
    int sum = 0;
    if (values != null) {
      for (int v : values) sum += v;
    }
    return sum;
  }

  public void rollDice() {
    boardGame.getDice().rollAllDice();
    diceRolled = true;
    LOGGER.info("Dice rolled: " + java.util.Arrays.toString(boardGame.getCurrentDiceValues()));
  }

  // For backward compatibility
  public int getLastDiceRoll() {
    int[] values = getLastDiceRolls();
    return (values != null && values.length > 0) ? values[0] : 0;
  }

  public MoveResult movePlayer(String playerName, int roll) {
    for (Player player : boardGame.getPlayers()) {
      if (player.getName().equals(playerName)) {
        int start = player.getCurrentPosition();
        int end = start + roll;
        String type = "normal";

        // Skip turn if player would overshoot 100
        if (end > 100) {
          return new MoveResult(start, start, "skip");
        }

        // Move the player to the new position first
        int steps = end - start;
        player.move(steps);

        // Check for snakes
        for (int[] snake : snakes) {
          if (end == snake[0]) {
            // Move player to snake tail
            steps = snake[1] - end;
            player.move(steps);
            end = snake[1];
            type = "snake";
            break;
          }
        }

        // Check for ladders only if it's a normal move
        if (type.equals("normal")) {
          for (int[] ladder : ladders) {
            if (end == ladder[0]) {
              // Move player to ladder top
              steps = ladder[1] - end;
              player.move(steps);
              end = ladder[1];
              type = "ladder";
              break;
            }
          }
        }

        return new MoveResult(start, end, type);
      }
    }
    return new MoveResult(0, 0, "normal");
  }

  public void nextSnakesAndLaddersPlayer() {
    boardGame.setCurrentPlayerIndex(
        (boardGame.getCurrentPlayerIndex() + 1) % boardGame.getPlayers().size());
    LOGGER.info("Next player: " + getCurrentSnakesAndLaddersPlayerName());
  }

  public int getPlayerPosition(String playerName) {
    for (Player player : boardGame.getPlayers()) {
      if (player.getName().equals(playerName)) {
        return player.getCurrentPosition();
      }
    }
    return 0;
  }

  public void loadSnakesAndLadderGame(String savePath) {
      this.boardGame = this.loadGame(savePath, false );
  }

  public void updateSnakesAndLaddersPosition(String playerName, int position) {
    for (Player player : boardGame.getPlayers()) {
      if (player.getName().equals(playerName)) {
        // Move the player to the correct tile
        int steps = position - player.getCurrentPosition();
        player.move(steps);
        LOGGER.info(playerName + " moved to position " + position);
        return;
      }
    }
  }

  public void loadGameFromPath(Path savePath) {
    try {
      BoardGame loadedGame = boardGameReader.readBoardGame(savePath);
      this.boardGame = loadedGame;
      this.gameStarted = true;
      for (Player player : loadedGame.getPlayers()) {
        updateSnakesAndLaddersPosition(player.getName(), player.getCurrentPosition());
      }
      boardGame.setCurrentPlayerIndex(loadedGame.getCurrentPlayerIndex());
      boardGame.notifyObservers();
      LOGGER.info("Game loaded from: " + savePath);
    } catch (IOException e) {
      LOGGER.severe("Failed to load game: " + e.getMessage());
    }
  }

  public static class MoveResult {
    public final int start;
    public final int end;
    public final String type; // "normal", "snake", "ladder"

    public MoveResult(int start, int end, String type) {
      this.start = start;
      this.end = end;
      this.type = type;
    }
  }
}
