package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TileTest {
    private Tile tile;
    private Tile nextTile;
    private Player player;
    private TileAction mockAction;

    @BeforeEach
    void setUp() {
        tile = new Tile(1);
        nextTile = new Tile(2);
        player = mock(Player.class);
        mockAction = mock(TileAction.class);
    }

    @Test
    void testConstructorWithValidId() {
        assertDoesNotThrow(() -> new Tile(1));
        assertEquals(1, tile.getId());
        assertNull(tile.getAction());
    }

    @Test
    void testConstructorWithAction() {
        Tile tileWithAction = new Tile(1, mockAction);
        assertEquals(1, tileWithAction.getId());
        assertEquals(mockAction, tileWithAction.getAction());
    }

    @Test
    void testConstructorWithNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> new Tile(-1));
    }

    @Test
    void testSetNextTile() {
        tile.setNextTile(nextTile);
        assertEquals(nextTile, tile.getNextTile());
    }

    @Test
    void testLandPlayer() {
        tile.landPlayer(player);
        assertEquals(player, tile.getCurrentPlayer());
    }

    @Test
    void testLandPlayerWithNull() {
        assertThrows(IllegalArgumentException.class, () -> tile.landPlayer(null));
    }

    @Test
    void testLandPlayerWithAction() {
        Tile tileWithAction = new Tile(1, mockAction);
        tileWithAction.landPlayer(player);
        verify(mockAction).performAction(player);
    }

    @Test
    void testLeavePlayer() {
        tile.landPlayer(player);
        tile.leavePlayer();
        assertNull(tile.getCurrentPlayer());
    }

    @Test
    void testIsLastTile() {
        assertTrue(tile.isLastTile());
        tile.setNextTile(nextTile);
        assertFalse(tile.isLastTile());
    }

    @Test
    void testGetDistanceToSameTile() {
        assertEquals(0, tile.getDistanceTo(tile));
    }

    @Test
    void testGetDistanceToNextTile() {
        tile.setNextTile(nextTile);
        assertEquals(1, tile.getDistanceTo(nextTile));
    }

    @Test
    void testGetDistanceToUnreachableTile() {
        Tile unreachableTile = new Tile(3);
        assertEquals(-1, tile.getDistanceTo(unreachableTile));
    }

    @Test
    void testGetDistanceToWithCycle() {
        // Create a cycle: tile1 -> tile2 -> tile3 -> tile1
        Tile tile1 = new Tile(1);
        Tile tile2 = new Tile(2);
        Tile tile3 = new Tile(3);
        
        tile1.setNextTile(tile2);
        tile2.setNextTile(tile3);
        tile3.setNextTile(tile1);
        
        assertEquals(-1, tile1.getDistanceTo(new Tile(4)));
    }

    @Test
    void testGetDistanceToNullTile() {
        assertThrows(IllegalArgumentException.class, () -> tile.getDistanceTo(null));
    }

    @Test
    void testEquals() {
        Tile sameTile = new Tile(1);
        Tile differentTile = new Tile(2);
        
        assertTrue(tile.equals(sameTile));
        assertFalse(tile.equals(differentTile));
        assertFalse(tile.equals(null));
        assertFalse(tile.equals(new Object()));
    }

    @Test
    void testHashCode() {
        Tile sameTile = new Tile(1);
        assertEquals(tile.hashCode(), sameTile.hashCode());
    }
} 