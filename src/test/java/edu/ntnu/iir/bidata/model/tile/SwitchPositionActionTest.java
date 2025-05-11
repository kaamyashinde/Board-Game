package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

class SwitchPositionActionTest {
    private SwitchPositionAction switchPositionAction;
    private Player mockCurrentPlayer;
    private Player mockPlayerInFront;
    private Tile mockCurrentTile;
    private Tile mockPlayerInFrontTile;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        mockCurrentPlayer = mock(Player.class);
        mockPlayerInFront = mock(Player.class);
        mockCurrentTile = mock(Tile.class);
        mockPlayerInFrontTile = mock(Tile.class);
        
        players = new ArrayList<>();
        players.add(mockCurrentPlayer);
        players.add(mockPlayerInFront);
        
        switchPositionAction = new SwitchPositionAction(players);
    }

    @Test
    void testGetDescription() {
        assertEquals("Switch position with the player in front of you", switchPositionAction.getDescription());
    }

    @Test
    void testExecuteActionWithPlayerInFront() {
        // Setup
        when(mockCurrentPlayer.getCurrentPosition()).thenReturn(5);
        when(mockPlayerInFront.getCurrentPosition()).thenReturn(8);
        when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockCurrentTile);
        when(mockPlayerInFront.getCurrentTile()).thenReturn(mockPlayerInFrontTile);

        // Execute
        switchPositionAction.executeAction(mockCurrentPlayer, mockCurrentTile);

        // Verify
        verify(mockCurrentPlayer).setCurrentTile(mockPlayerInFrontTile);
        verify(mockPlayerInFront).setCurrentTile(mockCurrentTile);
    }

    @Test
    void testExecuteActionNoPlayerInFront() {
        // Setup
        when(mockCurrentPlayer.getCurrentPosition()).thenReturn(8);
        when(mockPlayerInFront.getCurrentPosition()).thenReturn(5);
        when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockCurrentTile);
        when(mockPlayerInFront.getCurrentTile()).thenReturn(mockPlayerInFrontTile);

        // Execute
        switchPositionAction.executeAction(mockCurrentPlayer, mockCurrentTile);

        // Verify that no position switching occurred
        verify(mockCurrentPlayer, never()).setCurrentTile(any());
        verify(mockPlayerInFront, never()).setCurrentTile(any());
    }
} 