package edu.ntnu.iir.bidata.model.exception;

/**
 * A class that represents an exception for low money.
 *
 * @author Kaamya Shinde
 * @version 1.0
 */
public class LowMoneyException extends Exception {
  /** Constructs a new LowMoneyException with the message "Insufficient funds". */
  public LowMoneyException() {
    super("Insufficient funds");
  }
}
