package edu.ntnu.iir.bidata.model.tile.actions.game;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * A tile action that makes a player skip their next turn. This action is typically used to create a
 * penalty or setback for players who land on certain tiles in the game.
 *
 * @author kaamyashinde
 * @version 1.0
 */
public class LoseTurnAction implements TileAction {

  /**
   * Executes the action of making a player skip their next turn. This is done by setting the
   * player's skipNextTurn flag to true, which will be checked during the next turn to determine if
   * the player should be skipped.
   *
   * @param player      The player who landed on the tile
   * @param currentTile The tile the player landed on
   */
  @Override
  public void executeAction(Player player, Tile currentTile) {
    player.setSkipNextTurn(true);
  }

  /**
   * Returns a description of the action that will be performed. This description is used to inform
   * players about what will happen when they land on a tile with this action.
   *
   * @return A string describing the action
   */
  @Override
  public String getDescription() {
    return "Skip your next turn";
  }
} 