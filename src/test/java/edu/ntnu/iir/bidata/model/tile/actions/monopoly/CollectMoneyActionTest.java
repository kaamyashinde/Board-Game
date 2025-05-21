package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CollectMoneyActionTest {
    private CollectMoneyAction action;
    private SimpleMonopolyPlayer player;
    private Tile tile;

    @BeforeEach
    void setUp() {
        action = new CollectMoneyAction();
        player = new SimpleMonopolyPlayer("Test Player");
        tile = new Tile(0);
    }

    @Test
    void testExecuteAction() {
        int initialMoney = player.getMoney();
        action.executeAction(player, tile);
        assertEquals(initialMoney + 200, player.getMoney());
    }

    @Test
    void testGetDescription() {
        assertEquals("Collect 200 money", action.getDescription());
    }
} 