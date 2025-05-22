package edu.ntnu.iir.bidata.model.tile.core;

import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import java.util.List;
import java.util.logging.Logger;

/**
 * Factory class for creating tiles with special actions.
 */
public class TileFactory {
  private static final Logger LOGGER = Logger.getLogger(TileFactory.class.getName());

  private final List<Player> players;
  private final TileConfiguration tileConfig;

  public TileFactory(List<Player> players, TileConfiguration tileConfig) {
    this.players = players;
    this.tileConfig = tileConfig;
  }

  public Tile createTile(int position) {
    TileAction action = createSpecialAction(position);
    return new Tile(position, action);
  }

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