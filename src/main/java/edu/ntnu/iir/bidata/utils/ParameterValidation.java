package edu.ntnu.iir.bidata.utils;

/**
 * The class that handles the paramter validation.
 *
 * @author kamyashinde
 * @version 0.0.1
 */

public class ParameterValidation {
  /**
   * Ensuring that the input is a positive integer.
   *
   * @param numberToValidate The value that is to be validated.
   * @throws IllegalArgumentException Exception thrown if the integer is negative.
   */
  public static void validateZeroPositiveInteger(int numberToValidate, String object) throws IllegalArgumentException {
    if (numberToValidate < 0) {
      throw new IllegalArgumentException("The " + object + " must be a positive integer.");
    }
  }

  /**
   * Ensuring that the input is a non-zero positive integer.
   *
   * @param numberToValidate The value that is to be validated.
   * @throws IllegalArgumentException Exception thrown if the integer is zero or lower.
   */
  public static void validateNonZeroPositiveInteger(int numberToValidate, String object) throws IllegalArgumentException {
    if (numberToValidate <= 0) {
      throw new IllegalArgumentException("The " + object + " needs to be a non-zero positive integer.");
    }
  }
}