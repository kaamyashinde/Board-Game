package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.tile.actions.base.SafeSpotAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;

/**
 * Controller class specifically for Ludo game logic.
 */
public class LudoController extends BaseGameController {

  private static final Logger LOGGER = Logger.getLogger(LudoController.class.getName());

  // Ludo specific data
  private final Map<String, List<Integer>> playerTokenPositions = new HashMap<>();
  private final Map<String, List<Boolean>> playerTokenHome = new HashMap<>();
  private final Map<String, List<Boolean>> playerTokenFinished = new HashMap<>();
  private final int[] homePositions = {0, 13, 26, 39}; // Starting positions for each player
  private final int[] finishPositions = {50, 51, 52, 53}; // Finish positions for each player

  @Getter
  private boolean movingPiece = false;

  @Getter
  private boolean gameOver = false;

  public LudoController(BoardGame boardGame) {
    super(boardGame);
    LOGGER.info("LudoController initialized");
  }

  @Override
  public void setPlayerNames(List<String> playerNames) {
    super.setPlayerNames(playerNames);
    // Initialize token positions for all players
    for (String playerName : playerNames) {
      List<Integer> positions = new ArrayList<>();
      List<Boolean> home = new ArrayList<>();
      List<Boolean> finished = new ArrayList<>();

      // Initialize 4 tokens for each player
      for (int i = 0; i < 4; i++) {
        positions.add(-1); // -1 indicates token is in home
        home.add(true);
        finished.add(false);
      }

      playerTokenPositions.put(playerName, positions);
      playerTokenHome.put(playerName, home);
      playerTokenFinished.put(playerName, finished);
    }
  }

  @Override
  public void handlePlayerMove() {
    if (!diceRolled) {
      LOGGER.warning("Dice must be rolled before moving");
      return;
    }

    String currentPlayer = getCurrentPlayerName();
    int[] diceValues = boardGame.getCurrentDiceValues();
    int steps = diceValues[0]; // Ludo uses only one die

    // Get legal moves for the current player
    List<Integer> legalMoves = getLegalMoves(currentPlayer, steps);
    if (legalMoves.isEmpty()) {
      LOGGER.info(currentPlayer + " has no legal moves. Passing turn.");
      nextPlayer();
      return;
    }

    // If there's only one legal move, make it automatically
    if (legalMoves.size() == 1) {
      MoveResult result = moveToken(currentPlayer, legalMoves.get(0), steps);
      handleMoveResult(result);
    } else {
      // Multiple legal moves - UI should handle this
      movingPiece = true;
    }
  }

  /**
   * Returns a list of legal token indices for the current player and dice value
   */
  public List<Integer> getLegalMoves(String playerName, int diceValue) {
    List<Integer> legal = new ArrayList<>();
    List<Integer> positions = playerTokenPositions.get(playerName);
    List<Boolean> home = playerTokenHome.get(playerName);
    List<Boolean> finished = playerTokenFinished.get(playerName);

    for (int i = 0; i < positions.size(); i++) {
        if (finished.get(i)) {
            continue;
        }

      if (home.get(i) && diceValue == 6) {
        legal.add(i);
      } else if (!home.get(i)) {
        int currentPos = positions.get(i);
        int newPos = (currentPos + diceValue) % 52;

        // Check if move would enter finish area
        int playerIndex = playerNames.indexOf(playerName);
        int finishPos = finishPositions[playerIndex];
        if (currentPos < finishPos && newPos >= finishPos && newPos < finishPos + 6) {
          legal.add(i);
        } else if (newPos < finishPos) {
          legal.add(i);
        }
      }
    }
    return legal;
  }

  /**
   * Moves a token for the player and returns the result
   */
  public MoveResult moveToken(String playerName, int tokenIndex, int diceValue) {
    List<Integer> positions = playerTokenPositions.get(playerName);
    List<Boolean> home = playerTokenHome.get(playerName);
    List<Boolean> finished = playerTokenFinished.get(playerName);
    int start = positions.get(tokenIndex);
    int end = start;
    String type = "normal";

    if (home.get(tokenIndex) && diceValue == 6) {
      // Move out of home
      home.set(tokenIndex, false);
      end = homePositions[playerNames.indexOf(playerName)];
      positions.set(tokenIndex, end);
      type = "home";
    } else if (!home.get(tokenIndex) && !finished.get(tokenIndex)) {
      // Move on board
      end = (start + diceValue) % 52;

      // Check for finish
      int playerIdx = playerNames.indexOf(playerName);
      int finishPos = finishPositions[playerIdx];
      if (start < finishPos && end >= finishPos && end < finishPos + 6) {
        finished.set(tokenIndex, true);
        type = "finish";
      }

      // Capture logic
      if (!isSafeZone(end)) {
        for (String otherPlayer : playerTokenPositions.keySet()) {
            if (otherPlayer.equals(playerName)) {
                continue;
            }
          List<Integer> otherPositions = playerTokenPositions.get(otherPlayer);
          List<Boolean> otherHome = playerTokenHome.get(otherPlayer);
          List<Boolean> otherFinished = playerTokenFinished.get(otherPlayer);

          for (int i = 0; i < otherPositions.size(); i++) {
            if (!otherHome.get(i) && !otherFinished.get(i) && otherPositions.get(i) == end) {
              // Send captured token home
              otherHome.set(i, true);
              otherPositions.set(i, -1);
              type = "capture";
            }
          }
        }
      }
      positions.set(tokenIndex, end);
    }

    return new MoveResult(tokenIndex, start, end, type);
  }

  private void handleMoveResult(MoveResult result) {
    String currentPlayer = getCurrentPlayerName();

    switch (result.type) {
      case "home":
        LOGGER.info(currentPlayer + " moved token " + (result.tokenIndex + 1) + " out of home");
        break;
      case "finish":
        LOGGER.info(currentPlayer + "'s token " + (result.tokenIndex + 1) + " has finished!");
        checkForWinner(currentPlayer);
        break;
      case "capture":
        LOGGER.info(currentPlayer + " captured an opponent's token!");
        break;
      default:
        LOGGER.info(currentPlayer + " moved token " + (result.tokenIndex + 1) + " to position "
            + result.end);
    }

    // If player rolled a 6, they get another turn
    if (boardGame.getCurrentDiceValues()[0] == 6) {
      diceRolled = false; // Allow player to roll again
    } else {
      nextPlayer();
    }
  }

  private boolean isSafeZone(int pos) {
    // Check if the tile at this position has a SafeSpotAction
    return boardGame.getBoard().getTile(pos).getAction() instanceof SafeSpotAction;
  }

  private void checkForWinner(String playerName) {
    List<Boolean> finished = playerTokenFinished.get(playerName);
    boolean allFinished = true;
    for (boolean tokenFinished : finished) {
      if (!tokenFinished) {
        allFinished = false;
        break;
      }
    }

    if (allFinished) {
      LOGGER.info(playerName + " has won the game!");
      gameOver = true;
    }
  }

  // Getters for UI
  public List<Integer> getPlayerTokenPositions(String playerName) {
    return playerTokenPositions.get(playerName);
  }

  public List<Boolean> getPlayerTokenHome(String playerName) {
    return playerTokenHome.get(playerName);
  }

  public List<Boolean> getPlayerTokenFinished(String playerName) {
    return playerTokenFinished.get(playerName);
  }

  public boolean isMovingPiece() {
    return movingPiece;
  }

  public void setMovingPiece(boolean movingPiece) {
    this.movingPiece = movingPiece;
  }

  /**
   * Rolls the dice and returns the value
   */
  public int rollDice() {
    boardGame.getDice().rollAllDice();
    diceRolled = true;
    int[] values = boardGame.getCurrentDiceValues();
    return (values != null && values.length > 0) ? values[0] : 0;
  }

  /**
   * Gets the board game instance
   */
  public BoardGame getBoardGame() {
    return boardGame;
  }

  public static class MoveResult {

    public final int tokenIndex;
    public final int start;
    public final int end;
    public final String type; // "normal", "home", "finish", "capture"

    public MoveResult(int tokenIndex, int start, int end, String type) {
      this.tokenIndex = tokenIndex;
      this.start = start;
      this.end = end;
      this.type = type;
    }
  }
} 