package edu.ntnu.iir.bidata.model.dice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Dice class.
 * Note: The Dice class has been modified for Monopoly to always use 2 dice,
 * so tests have been updated accordingly.
 *
 * @author Durva
 */
public class DiceTest {

  @Test
  void testConstructorWithValidNumberOfDice() {
    // Test that creating a Dice instance does not throw an exception
    // Note: Regardless of input, Monopoly Dice always creates 2 dice
    Dice dice = new Dice(3);
    Assertions.assertEquals(2, dice.getLastRolledValues().length,
        "Monopoly dice should always have 2 dice");
  }

  @Test
  void testConstructorAlwaysCreatesTwoDice() {
    // Test that any positive number results in 2 dice for Monopoly
    Dice dice1 = new Dice(1);
    Dice dice2 = new Dice(5);
    Dice dice3 = new Dice(10);

    Assertions.assertEquals(2, dice1.getLastRolledValues().length);
    Assertions.assertEquals(2, dice2.getLastRolledValues().length);
    Assertions.assertEquals(2, dice3.getLastRolledValues().length);
  }

  @Test
  void testConstructorWithInvalidNumberOfDice() {
    // Test that a zero or negative number of dice causes an IllegalArgumentException
    // even though the actual implementation will use 2 dice for Monopoly
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Dice(0),
        "Zero dice should throw IllegalArgumentException");
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Dice(-2),
        "Negative number of dice should throw IllegalArgumentException");
  }

  @Test
  void testRollAllDiceAndGetLastRolledValues() {
    // Create a Dice instance (will always have 2 dice for Monopoly)
    Dice dice = new Dice(2);

    // Roll all dice
    dice.rollAllDice();
    int[] values = dice.getLastRolledValues();

    // Check that we got exactly 2 values (for Monopoly)
    Assertions.assertEquals(2, values.length,
        "Monopoly should always have exactly 2 dice");

    // Check that each die's value is between 1 and 6
    java.util.Arrays.stream(values).forEach(value ->
        Assertions.assertTrue(value >= 1 && value <= 6,
            "Die value should be between 1 and 6, but was: " + value)
    );
  }

  @Test
  void testSumOfRolledValues() {
    // Create a Dice instance (always 2 dice for Monopoly)
    Dice dice = new Dice(4); // Input ignored, still creates 2 dice
    dice.rollAllDice();

    // Calculate the sum by iterating over all dice values
    int[] values = dice.getLastRolledValues();
    int calculatedSum = java.util.Arrays.stream(values).sum();

    // Verify that the sum matches the method result
    Assertions.assertEquals(calculatedSum, dice.sumOfRolledValues(),
        "Sum of rolled values should match calculated sum");

    // For 2 dice, sum should be between 2 and 12
    Assertions.assertTrue(calculatedSum >= 2 && calculatedSum <= 12,
        "Sum of 2 dice should be between 2 and 12, but was: " + calculatedSum);
  }

  @Test
  void testMultipleRolls() {
    // Test that rolling multiple times can produce different results
    Dice dice = new Dice(2);

    // Perform multiple rolls to increase chance of different results
    boolean foundDifferentRolls = false;
    int[] firstRoll = null;

    // Try up to 50 rolls to find different results
    for (int attempt = 0; attempt < 50 && !foundDifferentRolls; attempt++) {
      dice.rollAllDice();
      int[] currentRoll = dice.getLastRolledValues();

      if (firstRoll == null) {
        firstRoll = currentRoll.clone();
      } else {
        // Check if current roll is different from first roll
        int[] finalFirstRoll = firstRoll;
        foundDifferentRolls = java.util.stream.IntStream.range(0, firstRoll.length)
            .anyMatch(i -> finalFirstRoll[i] != currentRoll[i]);
      }
    }

    Assertions.assertTrue(foundDifferentRolls,
        "Multiple rolls should eventually produce different results");
  }

  @Test
  void testDiceValuesRange() {
    // Test that dice values are always in valid range across multiple rolls
    Dice dice = new Dice(2);

    for (int i = 0; i < 20; i++) {
      dice.rollAllDice();
      int[] values = dice.getLastRolledValues();

      for (int value : values) {
        Assertions.assertTrue(value >= 1 && value <= 6,
            "Die value should be between 1 and 6, but was: " + value + " on roll " + (i + 1));
      }
    }
  }

  @Test
  void testSumRange() {
    // Test that sum of 2 dice is always in valid range
    Dice dice = new Dice(2);

    for (int i = 0; i < 20; i++) {
      dice.rollAllDice();
      int sum = dice.sumOfRolledValues();

      Assertions.assertTrue(sum >= 2 && sum <= 12,
          "Sum of 2 dice should be between 2 and 12, but was: " + sum + " on roll " + (i + 1));
    }
  }

  @Test
  void testEqualsAndHashCode() {
    // Test equals and hashCode methods
    Dice dice1 = new Dice(2);
    Dice dice2 = new Dice(5); // Still creates 2 dice
    Dice dice3 = new Dice(3); // Still creates 2 dice

    // All Monopoly dice should be equal (same number of dice)
    Assertions.assertEquals(dice1, dice2, "All Monopoly dice should be equal");
    Assertions.assertEquals(dice2, dice3, "All Monopoly dice should be equal");

    // Hash codes should be equal for equal objects
    Assertions.assertEquals(dice1.hashCode(), dice2.hashCode(),
        "Equal objects should have equal hash codes");
  }
}