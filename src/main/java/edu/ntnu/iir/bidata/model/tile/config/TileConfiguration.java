package edu.ntnu.iir.bidata.model.tile.config;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represents the configuration of tiles in a board game. Provides information about ladders,
 * snakes, move-back tiles, skip turn tiles, and switch places tiles based on the difficulty level.
 */
public class TileConfiguration {
  private static final Logger LOGGER = Logger.getLogger(TileConfiguration.class.getName());

  private final Map<Integer, Integer> ladderConfig;
  private final Map<Integer, Integer> snakeConfig;
  private final Map<Integer, Integer> moveBackConfig;
  private final int[] skipTurnPositions;
  private final int[] switchPlacesPositions;

  /**
   * Creates a new instance of the TileConfiguration class with default settings. This constructor
   * initializes the configuration for a medium-level game, which includes predefined ladders,
   * snakes, move back positions, skip turn positions, and switch places positions.
   */
  public TileConfiguration() {
    this("medium");
  }

  /**
   * Constructs a new TileConfiguration object with game tile configurations based on the specified
   * difficulty level. The configurations include ladders, snakes, move back positions, skip turn
   * positions, and switch places positions.
   *
   * @param level The difficulty level of the game. Accepted values are "easy", "medium", or "hard".
   *     If an invalid value is provided, the configuration defaults to "medium".
   */
  public TileConfiguration(String level) {
    ladderConfig = new HashMap<>();
    snakeConfig = new HashMap<>();
    moveBackConfig = new HashMap<>();
    int[] skipTurns = new int[] {};
    int[] switchPlaces = new int[] {};

    if ("easy".equalsIgnoreCase(level)) {
      LOGGER.info("Easy level configuration");
      // Easy level configuration
      ladderConfig.put(4, 16);
      ladderConfig.put(19, 40);
      ladderConfig.put(28, 47);
      ladderConfig.put(52, 70);
      ladderConfig.put(57, 85);
      ladderConfig.put(62, 80);

      snakeConfig.put(49, 31);
      snakeConfig.put(58, 24);
      snakeConfig.put(82, 78);
      // No move back, skip turn, or switch places for easy
      skipTurns = new int[] {};
      switchPlaces = new int[] {};
    } else if ("hard".equalsIgnoreCase(level)) {
      LOGGER.info("Hard level configuration");
      // Hard level configuration
      ladderConfig.put(4, 25);
      ladderConfig.put(42, 84);
      ladderConfig.put(72, 88);

      snakeConfig.put(35, 7);
      snakeConfig.put(47, 44);
      snakeConfig.put(57, 3);
      snakeConfig.put(65, 8);
      snakeConfig.put(70, 32);
      snakeConfig.put(85, 42);
      snakeConfig.put(89, 71);
      snakeConfig.put(94, 66);
      snakeConfig.put(99, 83);
      // No move back, skip turn, or switch places for hard
      skipTurns = new int[] {};
      switchPlaces = new int[] {};
    } else {
      LOGGER.info("Medium level configuration");
      // Medium (default) configuration
      ladderConfig.put(3, 36);
      ladderConfig.put(8, 12);
      ladderConfig.put(14, 26);
      ladderConfig.put(31, 73);
      ladderConfig.put(59, 80);
      ladderConfig.put(83, 97);
      ladderConfig.put(90, 92);

      snakeConfig.put(38, 2);
      snakeConfig.put(29, 11);
      snakeConfig.put(78, 15);
      snakeConfig.put(89, 86);
      snakeConfig.put(95, 75);
      snakeConfig.put(99, 41);

      moveBackConfig.put(10, 3);
      moveBackConfig.put(20, 2);
      skipTurns = new int[] {5};
      switchPlaces = new int[] {12};
    }
    skipTurnPositions = skipTurns;
    switchPlacesPositions = switchPlaces;
  }

  /**
   * Determines whether the specified position corresponds to the starting position of a ladder on
   * the game board.
   *
   * @param position The position on the game board to check.
   * @return true if the position is the start of a ladder; otherwise, false.
   */
  public boolean isLadderStart(int position) {
    return ladderConfig.containsKey(position);
  }

  /** Retrieves the ending position of a ladder, given its starting position. */
  public int getLadderEnd(int startPosition) {
    return ladderConfig.get(startPosition);
  }

  /**
   * Determines whether the specified position corresponds to the head of a snake. In a
   * snakes-and-ladders game, a player landing on this position would slide down to the snake's
   * tail.
   *
   * @param position The position on the game board to check.
   * @return true if the position corresponds to the head of a snake; otherwise, false.
   */
  public boolean isSnakeHead(int position) {
    return snakeConfig.containsKey(position);
  }

  /**
   * Retrieves the tail position of a snake, given its head position. In a snake-and-ladders game,
   * if a player lands on the snake's head, they will slide down to the snake's tail.
   *
   * @param headPosition The position of the snake's head on the game board.
   * @return The position of the snake's tail corresponding to the given head position. If the head
   *     position does not correspond to a snake, the behavior depends on the implementation (e.g.,
   *     it may throw an exception or return a default value).
   */
  public int getSnakeTail(int headPosition) {
    return snakeConfig.get(headPosition);
  }

  /**
   * Determines whether the specified position is configured as a "skip turn" tile. A "skip turn"
   * tile causes the player to lose their next turn.
   *
   * @param position The position on the game board to check.
   * @return true if the position is configured as a "skip turn" tile; otherwise, false.
   */
  public boolean isSkipTurn(int position) {
    return java.util.Arrays.stream(skipTurnPositions).anyMatch(pos -> pos == position);
  }

  /**
   * Determines whether the specified position is configured as a "move back" tile. A "move back"
   * tile requires the player to retreat a certain number of positions on the board.
   *
   * @param position The position on the game board to check.
   * @return true if the position is configured as a "move back" tile; otherwise, false.
   */
  public boolean isMoveBack(int position) {
    return moveBackConfig.containsKey(position);
  }

  /**
   * Retrieves the number of steps a player must move back if they land on a tile configured for a
   * move-back penalty.
   *
   * @param position The position on the game board to check.
   * @return The number of steps to move back for the given position. If the position is not
   *     configured for a move-back penalty, this method may return 0 or throw an exception,
   *     depending on the implementation.
   */
  public int getMoveBackSteps(int position) {
    return moveBackConfig.get(position);
  }

  /**
   * Determines whether the specified position is designated as a "switch places" tile. A "switch
   * places" tile allows players to exchange their positions on the board.
   *
   * @param position The position on the game board to check.
   * @return true if the position is designated as a "switch places" tile; otherwise, false.
   */
  public boolean isSwitchPlaces(int position) {
    return java.util.Arrays.stream(switchPlacesPositions).anyMatch(pos -> pos == position);
  }
}
