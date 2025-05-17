package edu.ntnu.iir.bidata.filehandling.board;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MonopolyBoardFactoryTest {
    
    @Test
    void testCreateBoard() {
        Board board = MonopolyBoardFactory.createBoard();
        
        // Test board size
        assertEquals(20, board.getSizeOfBoard());
        
        // Test special tiles
        assertTrue(board.getTile(0) instanceof GoTile);
        assertTrue(board.getTile(10) instanceof JailTile);
        assertTrue(board.getTile(15) instanceof FreeParkingTile);
        
        // Test circular connection
        assertNotNull(board.getTile(0).getNextTile());
        assertNotNull(board.getTile(19).getNextTile());
        assertEquals(board.getTile(0), board.getTile(19).getNextTile());
    }
    
    @Test
    void testPropertyGroups() {
        Board board = MonopolyBoardFactory.createBoard();
        
        // Test property groups
        for (int i = 1; i <= 8; i++) {
            assertNotNull(board.getTile(i));
        }
        
        for (int i = 11; i <= 19; i++) {
            assertNotNull(board.getTile(i));
        }
    }
} 