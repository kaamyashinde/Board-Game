package edu.ntnu.iir.bidata.model.tile.actions.base;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoToTileActionTest {

  private GoToTileAction goToTileAction;
  private Player mockPlayer;
  private Tile mockCurrentTile;
  private Tile mockTargetTile;

  @BeforeEach
  void setUp() {
    goToTileAction = new GoToTileAction(5);
    mockPlayer = mock(Player.class);
    mockCurrentTile = mock(Tile.class);
    mockTargetTile = mock(Tile.class);
  }

  @Test
  void testConstructorWithNegativeTileId() {
    assertThrows(IllegalArgumentException.class, () -> new GoToTileAction(-1));
  }

  @Test
  void testGetDescription() {
    assertEquals("Go to tile 5", goToTileAction.getDescription());
  }

  @Test
  void testExecuteActionSuccess() {
    // Setup
    when(mockCurrentTile.getNextTile()).thenReturn(mockTargetTile);
    when(mockTargetTile.getId()).thenReturn(5);

    // Execute
    goToTileAction.executeAction(mockPlayer, mockCurrentTile);

    // Verify
    verify(mockPlayer).setCurrentTile(mockTargetTile);
  }

  @Test
  void testExecuteActionTargetNotFound() {
    // Setup
    when(mockCurrentTile.getNextTile()).thenReturn(null);

    // Execute and verify
    assertThrows(GameException.class,
        () -> goToTileAction.executeAction(mockPlayer, mockCurrentTile));
  }
} 