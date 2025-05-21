package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;

public class PropertyTileTest {
    private PropertyTile propertyTile;
    private static final int ID = 1;
    private static final int PRICE = 200;
    private static final int RENT = 20;
    private static final int GROUP = 1;
    
    @BeforeEach
    void setUp() {
        propertyTile = new PropertyTile(ID, PRICE, RENT, GROUP);
    }
    
    @Test
    void testConstructor() {
        assertEquals(ID, propertyTile.getId());
        assertEquals(PRICE, propertyTile.getPrice());
        assertEquals(RENT, propertyTile.getRent());
        assertEquals(GROUP, propertyTile.getGroup());
        assertNull(propertyTile.getOwner());
    }
    
    @Test
    void testSetAndGetPrice() {
        int newPrice = 300;
        propertyTile.setPrice(newPrice);
        assertEquals(newPrice, propertyTile.getPrice());
    }
    
    @Test
    void testSetAndGetRent() {
        int newRent = 30;
        propertyTile.setRent(newRent);
        assertEquals(newRent, propertyTile.getRent());
    }
    
    @Test
    void testSetAndGetGroup() {
        int newGroup = 2;
        propertyTile.setGroup(newGroup);
        assertEquals(newGroup, propertyTile.getGroup());
    }
    
    @Test
    void testSetAndGetOwner() {
        SimpleMonopolyPlayer player = new SimpleMonopolyPlayer("Test Player");
        propertyTile.setOwner(player);
        assertEquals(player, propertyTile.getOwner());
    }
    
    @Test
    void testIsOwnedWhenNoOwner() {
        assertFalse(propertyTile.isOwned());
    }
    
    @Test
    void testIsOwnedWhenHasOwner() {
        SimpleMonopolyPlayer player = new SimpleMonopolyPlayer("Test Player");
        propertyTile.setOwner(player);
        assertTrue(propertyTile.isOwned());
    }
}
