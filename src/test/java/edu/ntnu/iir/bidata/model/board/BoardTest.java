package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardTest {
    private Board board;
    private Tile mockTile;
    private TileAction mockAction;

    @BeforeEach
    void setUp() {
        board = new Board(5);
        mockTile = mock(Tile.class);
        mockAction = mock(TileAction.class);
    }

    @Test
    void testConstructor() {
        // Test valid constructor
        Board validBoard = new Board(5);
        assertNotNull(validBoard);
        assertEquals(0, validBoard.getSizeOfBoard());

        // Test invalid constructor
        assertThrows(IllegalArgumentException.class, () -> new Board(0));
        assertThrows(IllegalArgumentException.class, () -> new Board(-1));
    }

    @Test
    void testAddTile() {
        // Test adding a valid tile
        assertTrue(board.addTile(0, mockAction));
        assertEquals(1, board.getSizeOfBoard());
        assertNotNull(board.getTile(0));

        // Test adding a tile with duplicate ID
        assertFalse(board.addTile(0, mockAction));
        assertEquals(1, board.getSizeOfBoard());

        // Test adding a tile with invalid ID
        assertThrows(IllegalArgumentException.class, () -> board.addTile(-1, mockAction));
    }

   
    @Test
    void testGetStartingTile() {
        // Test when board is empty
        assertNull(board.getStartingTile());

        // Test when board has tiles
        board.addTile(0, mockAction);
        assertNotNull(board.getStartingTile());
        assertEquals(0, board.getStartingTile().getId());
    }

    @Test
    void testGetEndingTile() {
        // Test when board is empty
        assertNull(board.getEndingTile());

        // Test when board has tiles
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        assertNotNull(board.getEndingTile());
        assertEquals(1, board.getEndingTile().getId());
    }

    @Test
    void testIsValidTileConnection() {
        // Setup: Add tiles and connect them
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        board.connectTiles(0, board.getTile(1));

        // Test valid connection
        assertTrue(board.isValidTileConnection(0, 1));

        // Test invalid connection
        assertFalse(board.isValidTileConnection(1, 0));
    }

    @Test
    void testGetPositionOnBoard() {
        // Test getting position when tile exists
        board.addTile(0, mockAction);
        assertNotNull(board.getPositionOnBoard(0));

        // Test getting position when tile doesn't exist
        assertNull(board.getPositionOnBoard(1));
    }

    @Test
    void testGetSizeOfBoard() {
        assertEquals(0, board.getSizeOfBoard());
        board.addTile(0, mockAction);
        assertEquals(1, board.getSizeOfBoard());
    }

    @Test
    void testGetTiles() {
        assertNotNull(board.getTiles());
        assertTrue(board.getTiles().isEmpty());
        
        board.addTile(0, mockAction);
        assertEquals(1, board.getTiles().size());
        assertTrue(board.getTiles().containsKey(0));
    }

    @Test
    void testGetTile() {
        // Test getting existing tile
        board.addTile(0, mockAction);
        assertNotNull(board.getTile(0));

        // Test getting non-existing tile
        assertNull(board.getTile(1));
    }
} 