package edu.ntnu.iir.bidata.model.dice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
* @author Durva
*/

public class DiceTest {

  @Test
  void testConstructorWithValidNumberOfDice() {
    // Test that creating a Dice instance with a valid number (e.g., 3) does not throw an exception
    Dice dice = new Dice(3);
    Assertions.assertEquals(3, dice.getLastRolledValues().length);
  }

  @Test
  void testConstructorWithInvalidNumberOfDice() {
    // Test that a zero or negative number of dice causes an IllegalArgumentException
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Dice(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Dice(-2));
  }

  @Test
  void testRollAllDiceAndGetLastRolledValues() {
    // Create a Dice instance with multiple dice
    int numberOfDice = 5;
    Dice dice = new Dice(numberOfDice);

    // Roll all dice
    dice.rollAllDice();
    int[] values = dice.getLastRolledValues();

    // Check that we got the correct number of values
    Assertions.assertEquals(numberOfDice, values.length);

    // Check that each die's value is between 1 and 6
    java.util.Arrays.stream(values).forEach(value ->
        Assertions.assertTrue(value >= 1 && value <= 6,
            "Die value should be between 1 and 6, but was: " + value)
    );
  }

  @Test
  void testSumOfRolledValues() {
    // Create a Dice instance
    int numberOfDice = 4;
    Dice dice = new Dice(numberOfDice);
    dice.rollAllDice();

    // Calculate the sum by iterating over all dice values
    int[] values = dice.getLastRolledValues();
    int calculatedSum = java.util.Arrays.stream(values).sum();

    // Verify that the sum matches the method result
    Assertions.assertEquals(calculatedSum, dice.sumOfRolledValues());
  }

  @Test
  void testMultipleRolls() {
    // Test that rolling multiple times produces different results
    Dice dice = new Dice(2);
    dice.rollAllDice();
    int[] firstRoll = dice.getLastRolledValues();
    dice.rollAllDice();
    int[] secondRoll = dice.getLastRolledValues();
    
    // It's possible but very unlikely that both rolls are identical
    boolean different = false;
    different = java.util.stream.IntStream.range(0, firstRoll.length)
        .anyMatch(i -> firstRoll[i] != secondRoll[i]);
    Assertions.assertTrue(different, "Multiple rolls should produce different results");
  }
}
