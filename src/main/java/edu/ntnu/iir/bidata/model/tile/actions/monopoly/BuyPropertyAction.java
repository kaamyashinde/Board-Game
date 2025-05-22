package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that represents the action of buying a property.
 *
 * @author Kaamya Shinde
 * @version 1.0
 */
public class BuyPropertyAction implements TileAction {
  /**
   * Executes the action of buying a property.
   *
   * @param player The player who is buying the property.
   * @param currentTile The current tile.
   */
  @Override
  public void executeAction(Player player, Tile currentTile) {
    if (player instanceof SimpleMonopolyPlayer) {
      try {
        SimpleMonopolyPlayer monopolyPlayer = (SimpleMonopolyPlayer) player;
        PropertyTile propertyTile = (PropertyTile) currentTile;
        if (propertyTile.isOwned()) {
          monopolyPlayer.payRent(propertyTile.getRent());
        } else {
          monopolyPlayer.buyProperty(propertyTile);
        }
      } catch (LowMoneyException e) {
        Logger.getLogger(BuyPropertyAction.class.getName()).log(Level.SEVERE, null, e);
      }
    }
  }

  /**
   * Returns the description of the action.
   *
   * @return The description of the action.
   */
  @Override
  public String getDescription() {
    return "Buy Property";
  }
}
