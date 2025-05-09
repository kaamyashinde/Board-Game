package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private List<Player> players;
    
    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player("Test Player 1"));
        players.add(new Player("Test Player 2"));
        board = new Board(25, players);
    }
    
    @Test
    void testBoardInitialization() {
        assertEquals(25, board.getTiles().size());
        for (int i = 0; i < 24; i++) {
            assertNotNull(board.getTiles().get(i).getNextTile());
            assertEquals(i + 1, board.getTiles().get(i).getNextTile().getId());
        }
        assertNull(board.getTiles().get(24).getNextTile());
    }
    
    @Test
    void testGetPositionOnBoard() {
        for (int i = 0; i < 25; i++) {
            assertEquals(i, board.getPositionOnBoard(i).getId());
        }
    }
    
    @Test
    void testInvalidBoardSize() {
        assertThrows(IllegalArgumentException.class, () -> new Board(0, players));
        assertThrows(IllegalArgumentException.class, () -> new Board(-1, players));
    }
    
    @Test
    void testTileConnections() {
        for (int i = 0; i < 24; i++) {
            Tile currentTile = board.getTiles().get(i);
            Tile nextTile = board.getTiles().get(i + 1);
            assertEquals(nextTile, currentTile.getNextTile());
        }
    }

    @Test
    void testSpecialTiles() {
        // Test Ladder at position 3
        assertNotNull(board.getPositionOnBoard(3).getAction());
        assertEquals("Climb up to tile 12", board.getPositionOnBoard(3).getAction().getDescription());

        // Test Skip Turn tile at position 5
        assertNotNull(board.getPositionOnBoard(5).getAction());
        assertEquals("Skip your next turn", board.getPositionOnBoard(5).getAction().getDescription());

        // Test Snake at position 8
        assertNotNull(board.getPositionOnBoard(8).getAction());
        assertEquals("Slide down to tile 4", board.getPositionOnBoard(8).getAction().getDescription());

        // Test Move Back tile at position 10
        assertNotNull(board.getPositionOnBoard(10).getAction());
        assertEquals("Move back 3 spaces", board.getPositionOnBoard(10).getAction().getDescription());

        // Test Switch Places tile at position 12
        assertNotNull(board.getPositionOnBoard(12).getAction());
        assertEquals("Switch places with Test Player 1", board.getPositionOnBoard(12).getAction().getDescription());

        // Test Ladder at position 15
        assertNotNull(board.getPositionOnBoard(15).getAction());
        assertEquals("Climb up to tile 22", board.getPositionOnBoard(15).getAction().getDescription());

        // Test Snake at position 18
        assertNotNull(board.getPositionOnBoard(18).getAction());
        assertEquals("Slide down to tile 7", board.getPositionOnBoard(18).getAction().getDescription());

        // Test Move Back tile at position 20
        assertNotNull(board.getPositionOnBoard(20).getAction());
        assertEquals("Move back 2 spaces", board.getPositionOnBoard(20).getAction().getDescription());

        // Test Ladder at position 22
        assertNotNull(board.getPositionOnBoard(22).getAction());
        assertEquals("Climb up to tile 25", board.getPositionOnBoard(22).getAction().getDescription());

        // Test Snake at position 24
        assertNotNull(board.getPositionOnBoard(24).getAction());
        assertEquals("Slide down to tile 16", board.getPositionOnBoard(24).getAction().getDescription());

        // Test normal tile (should have no action)
        assertNull(board.getPositionOnBoard(1).getAction());
    }
} 