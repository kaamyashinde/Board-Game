package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 * Base controller class that contains common game functionality. This class serves as a foundation
 * for specific game controllers.
 */
public abstract class BaseGameController {
  protected static final Logger LOGGER = Logger.getLogger(BaseGameController.class.getName());
  private static final String GAME_PATH = "src/main/resources/saved_games/";
  protected BoardGame boardGame;
  @Getter @Setter protected int currentPlayerIndex = 0;
  protected List<String> playerNames;
  @Getter @Setter protected boolean diceRolled = false;
  protected BaseGameController(BoardGame boardGame) {
    this.boardGame = boardGame;
    LOGGER.info("BaseGameController initialized");
  }

  /** Sets the player names for the game */
  public void setPlayerNames(List<String> playerNames) {
    this.playerNames = playerNames;
    LOGGER.info("Setting player names: " + playerNames);
  }

  /** Starts the game */
  public void startGame() {
    LOGGER.info("Starting new game");
    boardGame.startGame();
  }

  /** Gets the current player's name */
  public String getCurrentPlayerName() {
    return playerNames.get(currentPlayerIndex);
  }

  /** Moves to the next player */
  protected void nextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
    boardGame.setCurrentPlayerIndex(currentPlayerIndex);
    diceRolled = false;
  }

  /** Abstract method to handle player movement Must be implemented by specific game controllers */
  public abstract void handlePlayerMove();

  /** Saves a board game to be opened later. */
  public void saveGame(String gameName) {
    BoardGameFileWriterGson writer = new BoardGameFileWriterGson();
    Path savePath = Path.of(GAME_PATH + gameName + ".json");
    try {
      writer.writeBoardGame(boardGame, savePath);
    } catch (IOException e) {
      LOGGER.severe("Failed to save game: " + e.getMessage());
    }
  }

  /** Loads a board game from a file. */
  public BoardGame loadGame(String gameName) {
    BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
    Path savePath = Path.of(GAME_PATH + gameName + ".json");
    try {
      boardGame = reader.readBoardGame(savePath);
      LOGGER.info("Game loaded successfully");
      return boardGame;
    } catch (IOException e) {
      LOGGER.severe("Failed to load game: " + e.getMessage());
      return null;
    }
  }
}
