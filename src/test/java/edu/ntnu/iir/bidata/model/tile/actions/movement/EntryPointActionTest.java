package edu.ntnu.iir.bidata.model.tile.actions.movement;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntryPointActionTest {

    private Player mockPlayer;
    private EntryPointAction entryPointAction;
    private Tile mockTile;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        entryPointAction = new EntryPointAction(mockPlayer);
        mockTile = mock(Tile.class);
    }

    @Test
    void constructor_ShouldSetOwner() {
        assertEquals(mockPlayer, entryPointAction.getOwner());
    }

    @Test
    void executeAction_ShouldNotThrowException() {
        // Since executeAction is empty, we just verify it doesn't throw any exceptions
        assertDoesNotThrow(() -> entryPointAction.executeAction(mockPlayer, mockTile));
    }

    @Test
    void getDescription_ShouldReturnCorrectString() {
        // Arrange
        String playerName = "TestPlayer";
        when(mockPlayer.getName()).thenReturn(playerName);

        // Act
        String description = entryPointAction.getDescription();

        // Assert
        assertEquals("Entry point for " + playerName + "'s pieces", description);
    }

    @Test
    void getOwner_ShouldReturnCorrectPlayer() {
        assertEquals(mockPlayer, entryPointAction.getOwner());
    }
} 