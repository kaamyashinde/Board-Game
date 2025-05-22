package edu.ntnu.iir.bidata.model.dice;

import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class handling multiple instances of the class {@link Die}.
 *
 * @author kamyashinde
 * @version 1.0.1
 */
public class Dice {
  /** The ArrayList that will contain the different dice that are created. */
  private final ArrayList<Die> dice;

  /**
   * The constructor that takes in the number of dice as a parameter to create a certain number of
   * Dice in the list.
   *
   * <p>For Monopoly, this will always create exactly 2 dice regardless of input,
   * but still validates that the input is a positive number.
   *
   * @param numberOfDice the number of dice requested (must be positive, but will be overridden to 2 for Monopoly)
   */
  public Dice(int numberOfDice) throws IllegalArgumentException {
    // Validate the input parameter first, even though we'll override it
    ParameterValidation.validateNonZeroPositiveInteger(
        numberOfDice, "number of dice to create the list");

    // For Monopoly, always use 2 dice after validation
    int actualNumberOfDice = 2;

    dice = new ArrayList<>();
    java.util.stream.IntStream.range(0, actualNumberOfDice).forEach(i -> dice.add(new Die()));
  }

  /**
   * The method responsible to roll all of the dice and update their corresponding last rolled
   * value.
   */
  public void rollAllDice() {
    dice.forEach(Die::roll);
  }

  /**
   * Get the last rolled value of all the dice.
   *
   * @return the last rolled value of all the dice.
   */
  public int[] getLastRolledValues() {
    return dice.stream().mapToInt(Die::getLastRolledValue).toArray();
  }

  /**
   * Sum of all the rolled values stored in the dice list.
   *
   * @return the sum of the values rolled by all the dice.
   */
  public int sumOfRolledValues() {
    return dice.stream().mapToInt(Die::getLastRolledValue).sum();
  }

  /**
   * Computes the hash code for the current Dice object. The hash code is calculated based on the
   * size of the dice list to ensure consistency with the equals method.
   *
   * @return the computed hash code for this object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(dice.size()); // Include same fields as in equals
  }

  /**
   * Compares this Dice object to another object for equality. Two Dice objects are considered equal
   * if they belong to the same class and have the same number of dice in their list.
   *
   * @param obj the object to be compared for equality with this Dice object
   * @return true if the specified object is equal to this Dice object; false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Dice other = (Dice) obj;
    return this.dice.size() == other.dice.size(); // Include any other important fields
  }
}
