package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
/**
 * A class for the different board games.
 * @author kaamyashinde
 * @version 0.0.1
 */
public class BoardGame {
  private final Board board;
  private final ArrayList<Player> players;
  private final Dice dice;
  private Player currentPlayer;

  /**
   * Add a player to the list of players.
   *
   * @param player the player to be added.
   */
  public void addPlayer(Player player) {
    players.add(player);
  }

  /**
   * Play the game for the current player by rolling the dice and moving the player a certain amount of steps.
   */
  public void play() {
    dice.rollAllDice();
    int steps = dice.sumOfRolledValues();
    currentPlayer.moveSteps(steps);
    //give the turn to the next player.
  }

  /**
   * Calculate the winner.
   * @author Durva
   */
  public void getWinner() {
    //perhaps needs to be in the play option so that it is checked each time a player plays the game?
    // - durva: i think it should be in play option we can move it later
    //need to check current player tile position.
    int lastTileId = board.getTiles().size() - 1;
    for (Player player : players) {
      if (player.getCurrentTile().getId() == lastTileId) {
        System.out.println("The winner is: " + player.getName());
      }
    }
  }

}