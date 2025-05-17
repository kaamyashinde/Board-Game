package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FreeParkingTileTest {
    
    @Test
    void testFreeParkingTileCreation() {
        FreeParkingTile freeParkingTile = new FreeParkingTile(15);
        assertNotNull(freeParkingTile);
        assertEquals(15, freeParkingTile.getId());
        assertNull(freeParkingTile.getAction()); // Free Parking has no action
    }
} 