package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
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
    void testConstructor_WithValidSize() {
        Board newBoard = new Board(5);

        assertNotNull(newBoard);
        assertEquals(5, newBoard.getSizeOfBoard());
        assertNotNull(newBoard.getTiles());
        assertTrue(newBoard.getTiles().isEmpty());
    }

    @Test
    void testConstructor_WithLargeSize() {
        Board largeBoard = new Board(1000);

        assertEquals(1000, largeBoard.getSizeOfBoard());
        assertTrue(largeBoard.getTiles().isEmpty());
    }

    @Test
    void testConstructor_WithInvalidSize_Zero() {
        assertThrows(IllegalArgumentException.class, () -> new Board(0));
    }

    @Test
    void testConstructor_WithInvalidSize_Negative() {
        assertThrows(IllegalArgumentException.class, () -> new Board(-1));
        assertThrows(IllegalArgumentException.class, () -> new Board(-100));
    }

    @Test
    void testAddTile_WithAction_Success() {
        boolean result = board.addTile(0, mockAction);

        assertTrue(result);
        assertTrue(board.getTiles().containsKey(0));
        assertNotNull(board.getTile(0));
        assertEquals(0, board.getTile(0).getId());
    }

    @Test
    void testAddTile_WithNullAction_Success() {
        boolean result = board.addTile(5, null);

        assertTrue(result);
        assertTrue(board.getTiles().containsKey(5));
        assertNotNull(board.getTile(5));
    }

    @Test
    void testAddTile_WithDuplicateId_ReturnsFalse() {
        board.addTile(3, mockAction);

        boolean result = board.addTile(3, mockAction);

        assertFalse(result);
        assertEquals(1, board.getTiles().size());
    }

    @Test
    void testAddTile_WithNegativeId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> board.addTile(-1, mockAction));
        assertThrows(IllegalArgumentException.class, () -> board.addTile(-5, null));
    }

    @Test
    void testAddTile_WithTileObject_Success() {
        Tile goTile = new GoTile(0);

        assertDoesNotThrow(() -> board.addTile(goTile));
        assertEquals(goTile, board.getTile(0));
        assertTrue(board.getTiles().containsKey(0));
    }

    @Test
    void testAddTile_WithTileObject_DuplicateId_ThrowsException() {
        Tile goTile1 = new GoTile(0);
        Tile goTile2 = new GoTile(0);

        board.addTile(goTile1);

        assertThrows(GameException.class, () -> board.addTile(goTile2));
    }

    @Test
    void testAddTile_WithTileObject_NegativeId_ThrowsException() {
        when(mockTile.getId()).thenReturn(-1);

        assertThrows(IllegalArgumentException.class, () -> board.addTile(mockTile));
    }

    @Test
    void testConnectTiles_Success() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        Tile tile0 = board.getTile(0);
        Tile tile1 = board.getTile(1);

        board.connectTiles(0, tile1);

        assertEquals(tile1, tile0.getNextTile(1));
    }

    @Test
    void testConnectTiles_WithNullNextTile_NoConnection() {
        board.addTile(0, mockAction);

        board.connectTiles(0, null);

        assertThrows(GameException.class, () -> board.getTile(0).getNextTile(1));
    }

    @Test
    void testConnectTiles_WithNonExistentFromTile_NoException() {
        board.addTile(1, mockAction);

        assertDoesNotThrow(() -> board.connectTiles(999, board.getTile(1)));
    }

    @Test
    void testConnectTiles_BothTilesNull_NoException() {
        assertDoesNotThrow(() -> board.connectTiles(0, null));
    }

    @Test
    void testGetStartingTile_WhenExists() {
        board.addTile(0, mockAction);

        Tile startingTile = board.getStartingTile();

        assertEquals(board.getTile(0), startingTile);
        assertNotNull(startingTile);
    }

    @Test
    void testGetStartingTile_WhenNotExists() {
        Tile startingTile = board.getStartingTile();

        assertNull(startingTile);
    }

    @Test
    void testGetEndingTile_WhenExists() {
        board.addTile(BOARD_SIZE - 1, mockAction);

        Tile endingTile = board.getEndingTile();

        assertEquals(board.getTile(BOARD_SIZE - 1), endingTile);
        assertNotNull(endingTile);
    }

    @Test
    void testGetEndingTile_WhenNotExists() {
        Tile endingTile = board.getEndingTile();

        assertNull(endingTile);
    }

    @Test
    void testIsValidTileConnection_ValidConnection() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        board.connectTiles(0, board.getTile(1));

        boolean isValid = board.isValidTileConnection(0, 1);

        assertTrue(isValid);
    }

    @Test
    void testIsValidTileConnection_InvalidConnection() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);

        boolean isValid = board.isValidTileConnection(0, 1);

        assertFalse(isValid);
    }

    @Test
    void testIsValidTileConnection_NonExistentTiles() {
        boolean isValid = board.isValidTileConnection(999, 998);

        assertFalse(isValid);
    }

    @Test
    void testIsValidTileConnection_ExceptionHandling() {
        board.addTile(0, mockAction);

        boolean isValid = board.isValidTileConnection(0, 1);

        assertFalse(isValid);
    }

    @Test
    void testGetPositionOnBoard_ValidId() {
        board.addTile(5, mockAction);

        Tile tile = board.getPositionOnBoard(5);

        assertEquals(board.getTile(5), tile);
        assertNotNull(tile);
    }

    @Test
    void testGetPositionOnBoard_InvalidId() {
        Tile tile = board.getPositionOnBoard(999);

        assertNull(tile);
    }

    @Test
    void testGetTile_ValidId() {
        board.addTile(7, mockAction);

        Tile tile = board.getTile(7);

        assertNotNull(tile);
        assertEquals(7, tile.getId());
    }

    @Test
    void testGetTile_InvalidId() {
        Tile tile = board.getTile(999);

        assertNull(tile);
    }

    @Test
    void testGetSizeOfBoard() {
        assertEquals(BOARD_SIZE, board.getSizeOfBoard());

        Board smallBoard = new Board(3);
        assertEquals(3, smallBoard.getSizeOfBoard());
    }

    @Test
    void testGetTiles() {
        assertTrue(board.getTiles().isEmpty());

        board.addTile(0, mockAction);
        board.addTile(5, null);

        assertEquals(2, board.getTiles().size());
        assertTrue(board.getTiles().containsKey(0));
        assertTrue(board.getTiles().containsKey(5));
    }

    @Test
    void testHashCode() {
        Board board1 = new Board(5);
        Board board2 = new Board(5);

        assertEquals(board1.hashCode(), board2.hashCode());

        board1.addTile(0, mockAction);
        assertNotEquals(board1.hashCode(), board2.hashCode());
    }

    @Test
    void testEquals_SameObject() {
        assertTrue(board.equals(board));
    }

    @Test
    void testEquals_NullObject() {
        assertFalse(board.equals(null));
    }

    @Test
    void testEquals_DifferentClass() {
        assertFalse(board.equals("string"));
    }

    @Test
    void testEquals_SameSizeEmptyBoards() {
        Board board1 = new Board(BOARD_SIZE);
        Board board2 = new Board(BOARD_SIZE);

        assertTrue(board1.equals(board2));
        assertTrue(board2.equals(board1));
    }

    @Test
    void testEquals_DifferentSizes() {
        Board board1 = new Board(5);
        Board board2 = new Board(10);

        assertFalse(board1.equals(board2));
        assertFalse(board2.equals(board1));
    }

    @Test
    void testEquals_SameSizeWithSameTiles() {
        Board board1 = new Board(BOARD_SIZE);
        Board board2 = new Board(BOARD_SIZE);

        board1.addTile(0, mockAction);
        board1.addTile(5, null);
        board2.addTile(0, mockAction);
        board2.addTile(5, null);

        assertTrue(board1.equals(board2));
    }

    @Test
    void testEquals_SameSizeWithDifferentTiles() {
        Board board1 = new Board(BOARD_SIZE);
        Board board2 = new Board(BOARD_SIZE);

        board1.addTile(0, mockAction);
        board2.addTile(1, mockAction);

        assertFalse(board1.equals(board2));
    }

    @Test
    void testComplexScenario_FullBoardSetup() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            assertTrue(board.addTile(i, i % 2 == 0 ? mockAction : null));
        }

        for (int i = 0; i < BOARD_SIZE - 1; i++) {
            board.connectTiles(i, board.getTile(i + 1));
        }

        assertEquals(BOARD_SIZE, board.getTiles().size());
        assertNotNull(board.getStartingTile());
        assertNotNull(board.getEndingTile());

        for (int i = 0; i < BOARD_SIZE - 1; i++) {
            assertTrue(board.isValidTileConnection(i, i + 1));
        }
    }

    @Test
    void testConnectTiles_ChainedConnections() {
        board.addTile(0, mockAction);
        board.addTile(1, mockAction);
        board.addTile(2, mockAction);

        board.connectTiles(0, board.getTile(1));
        board.connectTiles(1, board.getTile(2));

        assertTrue(board.isValidTileConnection(0, 1));
        assertTrue(board.isValidTileConnection(1, 2));
        assertFalse(board.isValidTileConnection(0, 2));
    }

    @Test
    void testBoundaryValues() {
        Board minBoard = new Board(1);
        assertEquals(1, minBoard.getSizeOfBoard());

        minBoard.addTile(0, mockAction);
        assertEquals(minBoard.getStartingTile(), minBoard.getEndingTile());
    }

    @Test
    void testAddTile_EdgeCases() {
        assertTrue(board.addTile(0, mockAction));
        assertTrue(board.addTile(BOARD_SIZE - 1, mockAction));

        assertFalse(board.addTile(0, null));
        assertFalse(board.addTile(BOARD_SIZE - 1, mockAction));
    }

    @Test
    void testGetTiles_ModifiabilityIsolation() {
        board.addTile(0, mockAction);
        var tiles = board.getTiles();

        assertEquals(1, tiles.size());

        board.addTile(1, null);
        assertEquals(2, board.getTiles().size());
    }
}