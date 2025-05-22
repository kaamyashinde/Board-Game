package edu.ntnu.iir.bidata.model.exception;

/** Custom exception class for game-specific errors. */
public class GameException extends RuntimeException {

  /**
   * Constructs a new GameException with the specified detail message.
   *
   * @param message the detail message explaining the nature of the exception
   */
  public GameException(String message) {
    super(message);
  }
}
