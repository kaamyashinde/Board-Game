package edu.ntnu.iir.bidata.model.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.util.List;

public class SimpleMonopolyPlayerTest {
    private SimpleMonopolyPlayer player;
    private static final String PLAYER_NAME = "Test Player";
    private static final int INITIAL_MONEY = 1500;
    
    @BeforeEach
    void setUp() {
        player = new SimpleMonopolyPlayer(PLAYER_NAME);
    }
    
    @Test
    void testConstructor() {
        assertEquals(PLAYER_NAME, player.getName());
        assertEquals(INITIAL_MONEY, player.getMoney());
        assertNotNull(player.getOwnedProperties());
        assertTrue(player.getOwnedProperties().isEmpty());
    }
    
    @Test
    void testGetMoney() {
        assertEquals(INITIAL_MONEY, player.getMoney());
    }
    
    @Test
    void testGetOwnedProperties() {
        List<PropertyTile> properties = player.getOwnedProperties();
        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }
}