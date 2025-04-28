package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    
    @BeforeEach
    void setUp() {
        board = new Board(10);
    }
    
    @Test
    void testBoardInitialization() {
        assertEquals(10, board.getTiles().size());
        for (int i = 0; i < 9; i++) {
            assertNotNull(board.getTiles().get(i).getNextTile());
            assertEquals(i + 1, board.getTiles().get(i).getNextTile().getId());
        }
        assertNull(board.getTiles().get(9).getNextTile());
    }
    
    @Test
    void testGetPositionOnBoard() {
        for (int i = 0; i < 10; i++) {
            assertEquals(i, board.getPositionOnBoard(i).getId());
        }
    }
    
    @Test
    void testInvalidBoardSize() {
        assertThrows(IllegalArgumentException.class, () -> new Board(0));
        assertThrows(IllegalArgumentException.class, () -> new Board(-1));
    }
    
    @Test
    void testTileConnections() {
        for (int i = 0; i < 9; i++) {
            Tile currentTile = board.getTiles().get(i);
            Tile nextTile = board.getTiles().get(i + 1);
            assertEquals(nextTile, currentTile.getNextTile());
        }
    }
} 