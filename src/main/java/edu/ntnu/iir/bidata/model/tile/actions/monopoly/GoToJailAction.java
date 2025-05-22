package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * A tile action that moves a player to the Jail tile.
 *
 * @author kaamyashinde
 * @version 1.0
 */
public class GoToJailAction implements TileAction {
  private final int jailTileId;

  /**
   * Constructs a GoToJailAction with a default jail tile ID.
   *
   * <p>This constructor initializes the action to move players to the jail tile with the default ID
   * of 10.
   */
  public GoToJailAction() {
    this(10); // Default jail tile ID
  }

  /**
   * Constructs a GoToJailAction with the specified jail tile ID.
   *
   * <p>This constructor initializes the action to move players to the specified jail tile.
   *
   * @param jailTileId The ID of the jail tile to which players will be sent.
   */
  public GoToJailAction(int jailTileId) {
    this.jailTileId = jailTileId;
  }

  /**
   * Executes the action of sending a player to the designated Jail tile.
   *
   * <p>If the player is an instance of SimpleMonopolyPlayer, the player is sent to jail, and their
   * current tile is updated to the Jail tile identified by the jailTileId.
   *
   * @param player The player for whom the action is executed.
   * @param tile The starting tile from which the search for the Jail tile begins.
   */
  @Override
  public void executeAction(Player player, Tile tile) {
    if (player instanceof SimpleMonopolyPlayer) {
      ((SimpleMonopolyPlayer) player).goToJail();
      // Move player to the jail tile
      Tile current = tile;
      while (current != null && current.getId() != jailTileId) {
        current = current.getNextTile();
      }
      if (current != null) {
        player.setCurrentTile(current);
      }
    }
  }

  /**
   * Returns the description of the "Go to Jail" action.
   *
   * @return The description of the action, which is "Go to Jail".
   */
  @Override
  public String getDescription() {
    return "Go to Jail";
  }

  /**
   * Retrieves the ID of the Jail tile associated with this action.
   *
   * @return The ID of the Jail tile.
   */
  public int getJailTileId() {
    return jailTileId;
  }
}
