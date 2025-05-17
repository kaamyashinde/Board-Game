package edu.ntnu.iir.bidata.model.exception;

/**
 * Exception thrown when a player is not of the expected type.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class IllegalPlayerException extends Exception {
    /**
     * Constructs an IllegalPlayerException with the specified detail message.
     * 
     * @param message The detail message.
     */
    public IllegalPlayerException(String message) {
        super(message);
    }
}
