package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GoTileTest {
    
    @Test
    void testGoTileCreation() {
        GoTile goTile = new GoTile(0);
        assertNotNull(goTile);
        assertEquals(0, goTile.getId());
        assertNotNull(goTile.getAction());
    }
} 