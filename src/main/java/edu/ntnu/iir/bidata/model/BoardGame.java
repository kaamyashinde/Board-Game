package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
/**
 * A class for the different board games.
 * @author kaamyashinde
 * @version 0.0.1
 */
public class BoardGame {
  private final Board board;
  private final Dice dice;
  private ArrayList<Player> playerArrayList;
  private HashMap<Player, Integer> players;
  private Player currentPlayer;

  /**
   * The constructor that helps set up the initial parameters for the board game.
   *
   * @param numOfDices   to decide how many dices shall be rolled at one time.
   * @param numOfPlayers the number of players playing the game.
   * @param board        the board that is to be used.
   */
  public BoardGame(int numOfDices, int numOfPlayers, Board board) {
    this.dice = new Dice(numOfDices);
    this.players = new HashMap<>(numOfPlayers);
    this.board = board;
  }

  /**
   * Add a player to the list of players and initialise their score to zero.
   *
   * @param player the player to be added.
   */
  public void addPlayer(Player player) {
    this.players.put(player, 0);
  }

  /**
   * Play the game for the current player by rolling the dice and moving the player a certain amount of steps.
   */
  public void playCurrentPlayer() {
    dice.rollAllDice();
    currentPlayer.moveSteps(dice.sumOfRolledValues());
  }

  /**
   * Play the game for each of the players by iterating over them and updating the currentPlayer field and their score.
   */
  public void playGame() {
    for (Map.Entry<Player, Integer> player : players.entrySet()) {
      currentPlayer = player.getKey();
      playCurrentPlayer();
      //TODO update the score board.

    }

  }

  /**
   * Calculate the winner.
   *
   * @author Durva
   */
  public void getWinner() {
    //perhaps needs to be in the play option so that it is checked each time a player plays the game?
    // - durva: i think it should be in play option we can move it later
    //need to check current player tile position.
    int lastTileId = board.getTiles().size() - 1;
    for (Player player : playerArrayList) {
      if (player.getCurrentTile().getId() == lastTileId) {
        System.out.println("The winner is: " + player.getName());
      }
    }
  }
}