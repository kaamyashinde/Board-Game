package edu.ntnu.iir.bidata.model.tile.actions.snakeandladder;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LadderActionTest {

    private Player mockPlayer;
    private Tile mockCurrentTile;
    private Tile mockTargetTile;
    private LadderAction ladderAction;
    private static final int TOP_TILE_ID = 10;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockCurrentTile = mock(Tile.class);
        mockTargetTile = mock(Tile.class);
        ladderAction = new LadderAction(TOP_TILE_ID);
    }

    @Test
    void constructor_WithNegativeTopTileId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new LadderAction(-1));
    }

    @Test
    void constructor_WithZeroTopTileId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new LadderAction(0));
    }

    @Test
    void constructor_WithValidTopTileId_ShouldSetTopTileId() {
        assertEquals(TOP_TILE_ID, ladderAction.getTopTileId());
    }

    @Test
    void executeAction_WhenTargetTileFound_ShouldMovePlayer() {
        // Arrange
        when(mockCurrentTile.getId()).thenReturn(5);
        when(mockCurrentTile.getNextTile()).thenReturn(mockTargetTile);
        when(mockTargetTile.getId()).thenReturn(TOP_TILE_ID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");

        // Act
        ladderAction.executeAction(mockPlayer, mockCurrentTile);

        // Assert
        verify(mockPlayer).setCurrentTile(mockTargetTile);
    }

    @Test
    void executeAction_WhenTargetTileNotFound_ShouldNotMovePlayer() {
        // Arrange
        when(mockCurrentTile.getId()).thenReturn(5);
        when(mockCurrentTile.getNextTile()).thenReturn(null);

        // Act
        ladderAction.executeAction(mockPlayer, mockCurrentTile);

        // Assert
        verify(mockPlayer, never()).setCurrentTile(any());
    }

    @Test
    void getDescription_ShouldReturnCorrectString() {
        assertEquals("Climb up to tile " + TOP_TILE_ID, ladderAction.getDescription());
    }

    @Test
    void getTopTileId_ShouldReturnCorrectId() {
        assertEquals(TOP_TILE_ID, ladderAction.getTopTileId());
    }
} 