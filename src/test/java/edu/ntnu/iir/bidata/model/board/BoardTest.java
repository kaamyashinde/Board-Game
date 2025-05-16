package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardTest {

    @Mock
    private TileAction mockAction;
    @Mock
    private Tile mockTile;
    @Mock
    private Tile mockNextTile;

    private Board board;
    private static final int BOARD_SIZE = 10;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        board = new Board(BOARD_SIZE);
    }

    @Test
    void constructor_WithValidSize_ShouldInitializeBoard() {
        assertNotNull(board);
        assertEquals(BOARD_SIZE, board.getSizeOfBoard());
        assertNotNull(board.getTiles());
        assertTrue(board.getTiles().isEmpty());
    }

    @Test
    void constructor_WithInvalidSize_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Board(0));
        assertThrows(IllegalArgumentException.class, () -> new Board(-1));
    }

    @Test
    void addTile_WithValidParameters_ShouldAddTile() {
        assertTrue(board.addTile(0, mockAction));
        assertTrue(board.getTiles().containsKey(0));
        assertNotNull(board.getTiles().get(0));
    }

    @Test
    void addTile_WithDuplicateId_ShouldReturnFalse() {
        board.addTile(0, mockAction);
        assertFalse(board.addTile(0, mockAction));
    }

    @Test
    void addTile_WithNegativeId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> board.addTile(-1, mockAction));
    }

    @Test
    void connectTiles_WithValidTiles_ShouldConnect() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        Tile tile0 = board.getTile(0);
        Tile tile1 = board.getTile(1);
        
        board.connectTiles(0, tile1);
        
        assertNotNull(tile0.getNextTile(1));
        assertEquals(tile1, tile0.getNextTile(1));
    }

    @Test
    void connectTiles_WithNullTile_ShouldNotConnect() {
        board.addTile(0, mockAction);
        board.connectTiles(0, null);
        assertThrows(GameException.class, () -> board.getTile(0).getNextTile(1));
    }

    @Test
    void getStartingTile_ShouldReturnFirstTile() {
        board.addTile(0, mockAction);
        assertEquals(board.getTile(0), board.getStartingTile());
    }

    @Test
    void getEndingTile_ShouldReturnLastTile() {
        board.addTile(BOARD_SIZE - 1, mockAction);
        assertEquals(board.getTile(BOARD_SIZE - 1), board.getEndingTile());
    }

    @Test
    void isValidTileConnection_WithValidConnection_ShouldReturnTrue() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        board.connectTiles(0, board.getTile(1));
        
        assertTrue(board.isValidTileConnection(0, 1));
    }

    @Test
    void isValidTileConnection_WithInvalidConnection_ShouldReturnFalse() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        
        assertFalse(board.isValidTileConnection(0, 1));
    }

    @Test
    void getPositionOnBoard_WithValidId_ShouldReturnTile() {
        board.addTile(5, mockAction);
        assertEquals(board.getTile(5), board.getPositionOnBoard(5));
    }

    @Test
    void getPositionOnBoard_WithInvalidId_ShouldReturnNull() {
        assertNull(board.getPositionOnBoard(999));
    }

    @Test
    void getTile_WithValidId_ShouldReturnTile() {
        board.addTile(5, mockAction);
        assertNotNull(board.getTile(5));
    }

    @Test
    void getTile_WithInvalidId_ShouldReturnNull() {
        assertNull(board.getTile(999));
    }
} 