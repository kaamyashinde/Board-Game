package edu.ntnu.iir.bidata.model.tile.core;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import java.util.List;
import java.util.logging.Logger;

/** Factory class for creating tiles with special actions. */
public class TileFactory {
  private static final Logger LOGGER = Logger.getLogger(TileFactory.class.getName());

  private final List<Player> players;
  private final TileConfiguration tileConfig;

  /**
   * Constructs a TileFactory instance that is used to create tiles with specific actions based on
   * the provided configuration and players.
   *
   * @param players the list of players participating in the game
   * @param tileConfig the configuration that specifies the rules and settings for the tiles
   */
  public TileFactory(List<Player> players, TileConfiguration tileConfig) {
    this.players = players;
    this.tileConfig = tileConfig;
  }

  /**
   * Creates a new tile at the specified position with an associated action.
   *
   * @param position the position of the tile on the game board
   * @return the newly created Tile instance with the specified position and action
   */
  public Tile createTile(int position) {
    TileAction action = createSpecialAction(position);
    return new Tile(position, action);
  }

  /**
   * Creates a special action for the specified tile position based on the game board configuration.
   * Depending on the configuration, the action could be a {@link LadderAction} if the position is
   * the start of a ladder, a {@link SnakeAction} if the position is the head of a snake, or no
   * action if the position has no special behavior.
   *
   * @param position the position on the game board for which the special action needs to be created
   * @return the {@link TileAction} for the specified position, or null if the position has no
   *     associated special action
   */
  private TileAction createSpecialAction(int position) {
    if (tileConfig.isLadderStart(position)) {
      LOGGER.info("Creating LadderAction for tile " + position);
      return new LadderAction(tileConfig.getLadderEnd(position));
    } else if (tileConfig.isSnakeHead(position)) {
      LOGGER.info("Creating SnakeAction for tile " + position);
      return new SnakeAction(tileConfig.getSnakeTail(position));
    }
    return null;
  }
}
