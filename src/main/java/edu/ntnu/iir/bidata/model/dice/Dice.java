package edu.ntnu.iir.bidata.model.dice;

import java.util.ArrayList;

import edu.ntnu.iir.bidata.model.utils.ParameterValidation;

/**
 * Class handling multiple instances of the class {@link Die}.
 *
 * @author kamyashinde
 * @version 1.0.1
 */

public class Dice {
  /**
   * The ArrayList that will contain the different dice that are created.
   */
  private final ArrayList<Die> dice;

  /**
   * The constructor that takes in the number of dice as a parameter to
   * create a certain number of Dice in the list.
   * <p>
   * The use of Collections.nCopies was suggested by CoPilot when asked
   * for a replacement for the use of a for loop.
   * </p>
   *
   * @param numberOfDice the number of dice to be stored in the list
   */
  public Dice(int numberOfDice) throws IllegalArgumentException {
    ParameterValidation.validateNonZeroPositiveInteger(numberOfDice, "number of dice to create the list");
    dice = new ArrayList<>();
    for (int i = 0; i < numberOfDice; i++) {
      dice.add(new Die());
    }
  }

  /**
   * The method responsible to roll all of the dice and update their corresponding last rolled value.
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

}