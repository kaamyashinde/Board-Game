package edu.ntnu.iir.bidata.model.tile.actions.movement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HopFiveStepsActionTest {

  private HopFiveStepsAction hopFiveStepsAction;
  private Player mockPlayer;
  private Tile mockTile;

  @BeforeEach
  void setUp() {
    hopFiveStepsAction = new HopFiveStepsAction();
    mockPlayer = mock(Player.class);
    mockTile = mock(Tile.class);
  }

  @Test
  void testGetDescription() {
    assertEquals("Skipping ahead 5 steps", hopFiveStepsAction.getDescription());
  }

  @Test
  void testExecuteAction() {
    // Execute the action
    hopFiveStepsAction.executeAction(mockPlayer, mockTile);

    // Verify that player.move(5) was called exactly once
    verify(mockPlayer, times(1)).move(5);
  }
} 