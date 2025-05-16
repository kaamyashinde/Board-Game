package edu.ntnu.iir.bidata.model.tile.actions.snakeandladder;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnakeActionTest {

    private Player mockPlayer;
    private Tile mockCurrentTile;
    private Tile mockTargetTile;
    private SnakeAction snakeAction;
    private static final int TAIL_TILE_ID = 5;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockCurrentTile = mock(Tile.class);
        mockTargetTile = mock(Tile.class);
        snakeAction = new SnakeAction(TAIL_TILE_ID);
    }

    @Test
    void constructor_WithNegativeTailTileId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new SnakeAction(-1));
    }

    @Test
    void constructor_WithZeroTailTileId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new SnakeAction(0));
    }

    @Test
    void constructor_WithValidTailTileId_ShouldSetTailTileId() {
        assertEquals(TAIL_TILE_ID, snakeAction.getTailTileId());
    }

    @Test
    void executeAction_WhenTargetTileFound_ShouldMovePlayer() {
        // Arrange
        when(mockCurrentTile.getId()).thenReturn(10);
        when(mockCurrentTile.getNextTile()).thenReturn(mockTargetTile);
        when(mockTargetTile.getId()).thenReturn(TAIL_TILE_ID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");

        // Act
        snakeAction.executeAction(mockPlayer, mockCurrentTile);

        // Assert
        verify(mockPlayer).setCurrentTile(mockTargetTile);
    }

    @Test
    void executeAction_WhenTargetTileNotFound_ShouldNotMovePlayer() {
        // Arrange
        when(mockCurrentTile.getId()).thenReturn(10);
        when(mockCurrentTile.getNextTile()).thenReturn(null);

        // Act
        snakeAction.executeAction(mockPlayer, mockCurrentTile);

        // Assert
        verify(mockPlayer, never()).setCurrentTile(any());
    }

    @Test
    void getDescription_ShouldReturnCorrectString() {
        assertEquals("Slide down to tile " + TAIL_TILE_ID, snakeAction.getDescription());
    }

    @Test
    void getTailTileId_ShouldReturnCorrectId() {
        assertEquals(TAIL_TILE_ID, snakeAction.getTailTileId());
    }
} 