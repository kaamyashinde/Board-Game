package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;

public class BuyPropertyActionTest {
    private BuyPropertyAction action;
    private SimpleMonopolyPlayer player;
    private PropertyTile property;

    @BeforeEach
    void setUp() {
        action = new BuyPropertyAction();
        player = new SimpleMonopolyPlayer("Alice");
        property = new PropertyTile(1, 200, 50, 1);
    }

    @Test
    void testBuyUnownedProperty() {
        action.executeAction(player, property);

        assertEquals(player, property.getOwner());
        assertTrue(player.getOwnedProperties().contains(property));
        assertEquals(1300, player.getMoney()); // 1500 - 200
    }

    @Test
    void testPayRentOnOwnedProperty() {
        SimpleMonopolyPlayer owner = new SimpleMonopolyPlayer("Bob");
        property.setOwner(owner);

        player = new SimpleMonopolyPlayer("Alice");
        player.setMoney(500);

        action.executeAction(player, property);

        assertEquals(450, player.getMoney()); // 500 - 50 rent
        assertEquals(owner, property.getOwner());
    }

    @Test
    void testBuyPropertyWithInsufficientFunds() {
        player.setMoney(100); // Not enough to buy

        action.executeAction(player, property);

        // Should not buy property, money unchanged, owner still null
        assertNull(property.getOwner());
        assertFalse(player.getOwnedProperties().contains(property));
        assertEquals(100, player.getMoney());
    }

    @Test
    void testPayRentWithInsufficientFunds() {
        SimpleMonopolyPlayer owner = new SimpleMonopolyPlayer("Bob");
        property.setOwner(owner);

        player.setMoney(10); // Not enough to pay rent

        action.executeAction(player, property);

        // Money should remain the same, owner unchanged
        assertEquals(10, player.getMoney());
        assertEquals(owner, property.getOwner());
    }
}
