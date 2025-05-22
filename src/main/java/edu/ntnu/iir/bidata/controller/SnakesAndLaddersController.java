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
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;

/** Controller class specifically for Snakes and Ladders game logic. */
public class SnakesAndLaddersController extends BaseGameController {
  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersController.class.getName());

  private final BoardGameFileWriter boardGameWriter;
  private final BoardGameFileReader boardGameReader;
  private boolean gameStarted = false;
  private BoardGame boardGame;
  private final GameMediator mediator;
  private final TileConfiguration tileConfig;

  @Inject
  public SnakesAndLaddersController(BoardGame boardGame, BoardGameFileWriter boardGameWriter, BoardGameFileReader boardGameReader, GameMediator mediator, TileConfiguration tileConfig) {
    super(boardGame);
    this.boardGame = boardGame;
    this.boardGameWriter = boardGameWriter;
    this.boardGameReader = boardGameReader;
    this.mediator = mediator;
    this.tileConfig = tileConfig;
    LOGGER.info("SnakesAndLaddersController initialized");
  }

  @Override
  public void setPlayerNames(List<String> playerNames) {
    super.setPlayerNames(playerNames);
    // Initialize positions for all players
    playerNames.forEach(playerName ->
        boardGame.getPlayers().stream()
            .filter(player -> player.getName().equals(playerName))
            .findFirst()
            .ifPresent(player -> player.setCurrentTile(boardGame.getBoard().getTile(0)))
    );
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
        sum = java.util.Arrays.stream(values).sum();
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
    Player foundPlayer = boardGame.getPlayers().stream()
        .filter(player -> player.getName().equals(playerName))
        .findFirst()
        .orElse(null);
    if (foundPlayer != null) {
        int start = foundPlayer.getCurrentPosition();
        int end = start + roll;
        String type = "normal";
        if (end > 100) {
            end = 100;
        }
        int steps = end - start;
        foundPlayer.move(steps);
        if (tileConfig.isSnakeHead(end)) {
            int tail = tileConfig.getSnakeTail(end);
            steps = tail - end;
            foundPlayer.move(steps);
            end = tail;
            type = "snake";
        } else if (tileConfig.isLadderStart(end)) {
            int top = tileConfig.getLadderEnd(end);
            steps = top - end;
            foundPlayer.move(steps);
            end = top;
            type = "ladder";
        }
        return new MoveResult(start, end, type);
    }
    return new MoveResult(0, 0, "normal");
  }

  public void nextSnakesAndLaddersPlayer() {
    boardGame.setCurrentPlayerIndex(
        (boardGame.getCurrentPlayerIndex() + 1) % boardGame.getPlayers().size());
    LOGGER.info("Next player: " + getCurrentSnakesAndLaddersPlayerName());
  }

  public int getPlayerPosition(String playerName) {
    return boardGame.getPlayers().stream()
        .filter(player -> player.getName().equals(playerName))
        .map(Player::getCurrentPosition)
        .findFirst()
        .orElse(0);
  }

  public void loadSnakesAndLadderGame(String savePath) {
      this.boardGame = this.loadGame(savePath, false );
  }

  public void updateSnakesAndLaddersPosition(String playerName, int position) {
    boardGame.getPlayers().stream()
        .filter(player -> player.getName().equals(playerName))
        .findFirst()
        .ifPresent(player -> {
            int steps = position - player.getCurrentPosition();
            player.move(steps);
            LOGGER.info(playerName + " moved to position " + position);
        });
  }

  public void loadGameFromPath(Path savePath) {
    try {
      BoardGame loadedGame = boardGameReader.readBoardGame(savePath);
      this.boardGame = loadedGame;
      this.gameStarted = true;
      loadedGame.getPlayers().forEach(player ->
          updateSnakesAndLaddersPosition(player.getName(), player.getCurrentPosition())
      );
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
