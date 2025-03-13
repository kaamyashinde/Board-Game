import edu.ntnu.iir.bidata.model.dice.Dice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
* @author Durva
*/

public class DiceTest {

  @Test
  void testConstructorWithValidNumberOfDice() {
    // Test that creating a Dice instance with a valid number (e.g., 3) does not throw an exception.
    Dice dice = new Dice(3);
    // Roll the dice and check that each returned value is within the valid range.
    dice.rollAllDice();
    for (int i = 0; i < 3; i++) {
      int value = dice.getDie(i);
      Assertions.assertTrue(value >= 1 && value <= 6,
          "Die value should be between 1 and 6, but was: " + value);
    }
  }

  @Test
  void testConstructorWithInvalidNumberOfDice() {
    // Test that a zero or negative number of dice causes an IllegalArgumentException.
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Dice(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Dice(-2));
  }

  @Test
  void testRollAllDiceAndGetDie() {
    // Create a Dice instance with multiple dice.
    int numberOfDice = 5;
    Dice dice = new Dice(numberOfDice);

    // Roll all dice.
    dice.rollAllDice();

    // Check that each die's value is between 1 and 6.
    for (int i = 0; i < numberOfDice; i++) {
      int value = dice.getDie(i);
      Assertions.assertTrue(value >= 1 && value <= 6,
          "Die at index " + i + " should have a value between 1 and 6, but was: " + value);
    }
  }

  @Test
  void testGetDieWithInvalidIndex() {
    // Create a Dice instance with 2 dice.
    Dice dice = new Dice(2);
    dice.rollAllDice();

    // Negative index should throw an IllegalArgumentException (based on ParameterValidation).
    Assertions.assertThrows(IllegalArgumentException.class, () -> dice.getDie(-1));

    // An index equal to the number of dice should throw an IndexOutOfBoundsException.
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> dice.getDie(2));
  }

  @Test
  void testSumOfRolledValues() {
    // Create a Dice instance.
    int numberOfDice = 4;
    Dice dice = new Dice(numberOfDice);
    dice.rollAllDice();

    // Calculate the sum by iterating over all dice.
    int calculatedSum = 0;
    for (int i = 0; i < numberOfDice; i++) {
      calculatedSum += dice.getDie(i);
    }

    Assertions.assertEquals(dice.sumOfRolledValues(), calculatedSum);
  }
}
