package edu.ntnu.iir.bidata.model.tile.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for special tiles on the board.
 */
public class TileConfiguration {

  private final Map<Integer, Integer> ladderConfig;
  private final Map<Integer, Integer> snakeConfig;
  private final Map<Integer, Integer> moveBackConfig;
  private final int[] skipTurnPositions;
  private final int[] switchPlacesPositions;

  public TileConfiguration() {
    // Initialize default configuration
    ladderConfig = new HashMap<>();
    snakeConfig = new HashMap<>();
    moveBackConfig = new HashMap<>();

    // Ladder configurations
    ladderConfig.put(3, 12);
    ladderConfig.put(15, 22);
    ladderConfig.put(22, 25);

    // Snake configurations
    snakeConfig.put(8, 4);
    snakeConfig.put(18, 7);
    snakeConfig.put(24, 16);

    // Move back configurations
    moveBackConfig.put(10, 3);
    moveBackConfig.put(20, 2);

    // Skip turn positions
    skipTurnPositions = new int[]{5};

    // Switch places positions
    switchPlacesPositions = new int[]{12};
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