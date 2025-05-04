package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private static final int VALID_TILE_ID = 1;
    private static final int INVALID_TILE_ID = -1;
    private static final int BOARD_SIZE = 10;
    private TileAction mockTileAction;

    @BeforeEach
    void setUp() {
        board = new Board(BOARD_SIZE);
        mockTileAction = new TileAction() {
            @Override
            public void performAction(Player player) {
                // Mock implementation
            }

            @Override
            public String getDescription() {
                return "Mock Tile Action";
            }
        };
    }

    @Test
    void testConstructor_ValidSize() {
        assertNotNull(board);
        assertEquals(0, board.getSizeOfBoard());
    }

    @Test
    void testConstructor_InvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Board(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Board(-1);
        });
    }

    @Test
    void testAddTile_Success() {
        boolean result = board.addTile(VALID_TILE_ID, mockTileAction);
        assertTrue(result);
        assertNotNull(board.getPositionOnBoard(VALID_TILE_ID));
        assertEquals(1, board.getSizeOfBoard());
    }

    @Test
    void testAddTile_DuplicateId() {
        board.addTile(VALID_TILE_ID, mockTileAction);
        boolean result = board.addTile(VALID_TILE_ID, mockTileAction);
        assertFalse(result);
        assertEquals(1, board.getSizeOfBoard());
    }

    @Test
    void testAddTile_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            board.addTile(INVALID_TILE_ID, mockTileAction);
        });
    }

    @Test
    void testGetPositionOnBoard_ExistingTile() {
        board.addTile(VALID_TILE_ID, mockTileAction);
        assertNotNull(board.getPositionOnBoard(VALID_TILE_ID));
    }

    @Test
    void testGetPositionOnBoard_NonExistentTile() {
        assertNull(board.getPositionOnBoard(VALID_TILE_ID));
    }

    @Test
    void testGetSizeOfBoard() {
        assertEquals(0, board.getSizeOfBoard());
        board.addTile(VALID_TILE_ID, mockTileAction);
        assertEquals(1, board.getSizeOfBoard());
    }

    @Test
    void testGetTiles() {
        assertNotNull(board.getTiles());
        assertTrue(board.getTiles().isEmpty());
        
        board.addTile(VALID_TILE_ID, mockTileAction);
        assertEquals(1, board.getTiles().size());
        assertTrue(board.getTiles().containsKey(VALID_TILE_ID));
    }

    @Test
    void testGetTile() {
        board.addTile(VALID_TILE_ID, mockTileAction);
        assertNotNull(board.getTile(VALID_TILE_ID));
        assertNull(board.getTile(2)); // Non-existent tile
    }
} 