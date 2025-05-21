package edu.ntnu.iir.bidata.model.tile.config;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Configuration class for special tiles on the board.
 */
public class TileConfiguration {
  private static final Logger LOGGER = Logger.getLogger(TileConfiguration.class.getName());

  private final Map<Integer, Integer> ladderConfig;
  private final Map<Integer, Integer> snakeConfig;
  private final Map<Integer, Integer> moveBackConfig;
  private final int[] skipTurnPositions;
  private final int[] switchPlacesPositions;

  public TileConfiguration() {
    this("medium");
  }

  public TileConfiguration(String level) {
    ladderConfig = new HashMap<>();
    snakeConfig = new HashMap<>();
    moveBackConfig = new HashMap<>();
    int[] skipTurns = new int[]{};
    int[] switchPlaces = new int[]{};

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
      snakeConfig.put(88, 45);
      snakeConfig.put(82, 78);
      // No move back, skip turn, or switch places for easy
      skipTurns = new int[]{};
      switchPlaces = new int[]{};
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
      snakeConfig.put(90, 71);
      snakeConfig.put(94, 66);
      snakeConfig.put(99, 83);
      // No move back, skip turn, or switch places for hard
      skipTurns = new int[]{};
      switchPlaces = new int[]{};
    } else {
      LOGGER.info("Medium level configuration");
      // Medium (default) configuration
      ladderConfig.put(3, 12);
      ladderConfig.put(15, 22);
      ladderConfig.put(22, 25);

      snakeConfig.put(8, 4);
      snakeConfig.put(18, 7);
      snakeConfig.put(24, 16);

      moveBackConfig.put(10, 3);
      moveBackConfig.put(20, 2);
      skipTurns = new int[]{5};
      switchPlaces = new int[]{12};
    }
    skipTurnPositions = skipTurns;
    switchPlacesPositions = switchPlaces;
  }

  public boolean isLadderStart(int position) {
    return ladderConfig.containsKey(position);
  }

  public int getLadderEnd(int startPosition) {
    return ladderConfig.get(startPosition);
  }

  public boolean isSnakeHead(int position) {
    return snakeConfig.containsKey(position);
  }

  public int getSnakeTail(int headPosition) {
    return snakeConfig.get(headPosition);
  }

  public boolean isSkipTurn(int position) {
    for (int pos : skipTurnPositions) {
        if (pos == position) {
            return true;
        }
    }
    return false;
  }

  public boolean isMoveBack(int position) {
    return moveBackConfig.containsKey(position);
  }

  public int getMoveBackSteps(int position) {
    return moveBackConfig.get(position);
  }

  public boolean isSwitchPlaces(int position) {
    for (int pos : switchPlacesPositions) {
        if (pos == position) {
            return true;
        }
    }
    return false;
  }
} 