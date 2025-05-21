package edu.ntnu.iir.bidata.model.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for IllegalPlayerException.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class IllegalPlayerExceptionTest {

    @Test
    public void testIllegalPlayerExceptionMessage() {
        String expectedMessage = "Invalid player type";
        IllegalPlayerException exception = new IllegalPlayerException(expectedMessage);
        
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testIllegalPlayerExceptionWithNullMessage() {
        IllegalPlayerException exception = new IllegalPlayerException(null);
        assertNull(exception.getMessage());
    }
} 