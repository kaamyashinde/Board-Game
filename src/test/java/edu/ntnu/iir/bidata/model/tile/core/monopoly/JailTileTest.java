package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JailTileTest {
    
    @Test
    void testJailTileCreation() {
        JailTile jailTile = new JailTile(10);
        assertNotNull(jailTile);
        assertEquals(10, jailTile.getId());
        assertNull(jailTile.getAction());
    }
} 