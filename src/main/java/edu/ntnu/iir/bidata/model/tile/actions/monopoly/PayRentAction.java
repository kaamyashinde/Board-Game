package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PayRentAction implements TileAction{
    /**
     * Executes the action of paying rent.
     * 
     * @param player The player who is paying rent.
     * @param currentTile The current tile.
     */
    @Override
    public void executeAction(Player player, Tile currentTile) {
        if (player instanceof SimpleMonopolyPlayer){
            PropertyTile propertyTile = (PropertyTile) currentTile;
            try {
                ((SimpleMonopolyPlayer) player).payRent(propertyTile.getRent());
            } catch (LowMoneyException e) {
                Logger.getLogger(PayRentAction.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            Logger.getLogger(PayRentAction.class.getName()).log(Level.SEVERE, "Player is not a SimpleMonopolyPlayer");
        }

    }
    /**
     * Returns the description of the action.
     * 
     * @return The description of the action.
     */
    public String getDescription() {
        return "Pay Rent";
    }

}
