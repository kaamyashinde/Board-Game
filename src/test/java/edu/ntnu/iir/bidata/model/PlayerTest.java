package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private Tile tile;
    
    @BeforeEach
    void setUp() {
        player = new Player("Test Player");
        tile = new Tile(1, null);
    }
    
    @Test
    void testPlayerInitialization() {
        assertEquals("Test Player", player.getName());
        assertNull(player.getCurrentTile());
    }
    
    @Test
    void testPlaceOnTile() {
        player.placeOnTile(tile);
        assertEquals(tile, player.getCurrentTile());
    }
    
    @Test
    void testPlayerName() {
        player.setName("New Name");
        assertEquals("New Name", player.getName());
    }
    
    @Test
    void testPlayerTileUpdate() {
        Tile newTile = new Tile(2, null);
        player.placeOnTile(tile);
        player.placeOnTile(newTile);
        assertEquals(newTile, player.getCurrentTile());
    }
} 