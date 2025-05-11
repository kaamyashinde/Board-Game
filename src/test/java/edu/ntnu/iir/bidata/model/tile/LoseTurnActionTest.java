package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoseTurnActionTest {
    private LoseTurnAction loseTurnAction;
    private Player mockPlayer;
    private Tile mockTile;

    @BeforeEach
    void setUp() {
        loseTurnAction = new LoseTurnAction();
        mockPlayer = mock(Player.class);
        mockTile = mock(Tile.class);
    }

    @Test
    void testGetDescription() {
        assertEquals("Skip your next turn", loseTurnAction.getDescription());
    }

    @Test
    void testExecuteAction() {
        // Execute
        loseTurnAction.executeAction(mockPlayer, mockTile);

        // Verify
        verify(mockPlayer).setSkipNextTurn(true);
    }
} 