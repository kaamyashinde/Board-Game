package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GoToJailActionTest {
    private GoToJailAction action;
    private SimpleMonopolyPlayer player;
    private Tile tile;

    @BeforeEach
    void setUp() {
        action = new GoToJailAction();
        player = new SimpleMonopolyPlayer("Test Player");
        tile = new Tile(10);
    }

    @Test
    void testExecuteAction() {
        // TODO: Implement this test once goToJail() is implemented in SimpleMonopolyPlayer
        // action.executeAction(player, tile);
        // assertTrue(player.isInJail());
    }

    @Test
    void testGetDescription() {
        assertEquals("Go to Jail", action.getDescription());
    }
} 