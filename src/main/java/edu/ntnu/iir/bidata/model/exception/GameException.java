package edu.ntnu.iir.bidata.model.exception;

/**
 * Custom exception class for game-specific errors.
 */
public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
} 