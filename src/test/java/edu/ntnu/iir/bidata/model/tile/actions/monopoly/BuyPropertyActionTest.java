package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuyPropertyActionTest {
    private BuyPropertyAction action;
    private SimpleMonopolyPlayer player;
    private PropertyTile propertyTile;

    @BeforeEach
    void setUp() {
        action = new BuyPropertyAction();
        player = new SimpleMonopolyPlayer("Test Player");
        propertyTile = new PropertyTile(1, 100, 20, 0);
    }

    @Test
    void testExecuteActionOnUnownedProperty() {
        assertFalse(propertyTile.isOwned());
        action.executeAction(player, propertyTile);
        assertTrue(propertyTile.isOwned());
        assertEquals(player, propertyTile.getOwner());
    }

    @Test
    void testExecuteActionOnOwnedProperty() {
        SimpleMonopolyPlayer owner = new SimpleMonopolyPlayer("Owner");
        propertyTile.setOwner(owner);
        int initialMoney = player.getMoney();
        action.executeAction(player, propertyTile);
        assertEquals(initialMoney - propertyTile.getRent(), player.getMoney());
    }

    @Test
    void testGetDescription() {
        assertEquals("Buy Property", action.getDescription());
    }
}
