package edu.ntnu.iir.bidata.model.tile.actions.base;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SafeSpotActionTest {

    private Player mockPlayer;
    private Tile mockTile;
    private SafeSpotAction safeSpotAction;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockTile = mock(Tile.class);
        safeSpotAction = new SafeSpotAction();
    }

    @Test
    void executeAction_ShouldNotThrowException() {
        // Since executeAction is empty, we just verify it doesn't throw any exceptions
        assertDoesNotThrow(() -> safeSpotAction.executeAction(mockPlayer, mockTile));
    }

    @Test
    void executeAction_ShouldNotModifyPlayerOrTile() {
        // Act
        safeSpotAction.executeAction(mockPlayer, mockTile);

        // Assert - verify no interactions with player or tile
        verifyNoInteractions(mockPlayer);
        verifyNoInteractions(mockTile);
    }

    @Test
    void getDescription_ShouldReturnCorrectString() {
        String expectedDescription = "Safe spot - pieces cannot be captured here";
        assertEquals(expectedDescription, safeSpotAction.getDescription());
    }
} 