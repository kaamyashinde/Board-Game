package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class for the different board games.
 *
 * @author kaamyashinde
 * @version 0.0.1
 */
@AllArgsConstructor

public class BoardGame {
  private final Board board;
  private final Dice dice;
  private ArrayList<Player> playerArrayList;
  private HashMap<Player, Integer> players;
  private Player currentPlayer;
  private boolean playing;

  /**
   * The constructor that helps set up the initial parameters for the board game.
   *
   * @param numOfDices   to decide how many dices shall be rolled at one time.
   * @param numOfPlayers the number of players playing the game.
   * @param sizeOfBoard  how big the board is supposed to be.
   */
  public BoardGame(int numOfDices, int numOfPlayers, int sizeOfBoard) {
    this.dice = new Dice(numOfDices);
    this.players = new HashMap<>(numOfPlayers);
    this.board = new Board(sizeOfBoard);
  }

  public static void main(String[] args) {
    BoardGame game = new BoardGame(1, 2, 15);
    game.addPlayer(new Player("Kaamya"));
    game.addPlayer(new Player("Durva"));
    game.playGame();
  }

  /**
   * Add a player to the list of players and initialise their score to zero.
   *
   * @param player the player to be added.
   */
  public void addPlayer(Player player) {
    player.setCurrentTile(board.getTiles().get(0));
    this.players.put(player, 0);
  }

  /**
   * Initialising the game by initialising the currentPlayer value to the first player in the list.
   */
  public void initialiseGame() {
    currentPlayer = players.keySet().iterator().next();
    System.out.println("First player is: " + currentPlayer.getName());
  }

  /**
   * Play the game for the current player by rolling the dice and moving the player a certain amount of steps.
   * //TODO missing the use of nextTile logic
   */
  public void playCurrentPlayer() {
    System.out.println("-----------");
    System.out.println("Current player " + currentPlayer.getName());
    System.out.println("Current position: " + currentPlayer.getCurrentTile().getId());
    dice.rollAllDice();
    int newPositionOnBoard = currentPlayer.getCurrentTile().getId() + dice.sumOfRolledValues();
    if(newPositionOnBoard >= board.getTiles().size()){
      newPositionOnBoard = board.getTiles().size();
    }
    if (newPositionOnBoard >= board.getTiles().size()) {
      newPositionOnBoard = board.getTiles().size();
      System.out.println("New position: " + newPositionOnBoard);

      System.out.println("Player " + currentPlayer.getName() + " has won!");
      playing = false;
    } else {
      System.out.println("New position: " + newPositionOnBoard);

      currentPlayer.setCurrentTile(board.getPositionOnBoard(newPositionOnBoard));
    }
  }

  /**
   * Play the game for each of the players by iterating over them and updating the currentPlayer field and their score.
   */
  public void playGame() {
    playing = true;
    while (playing) {
      for (Map.Entry<Player, Integer> player : players.entrySet()) {
        currentPlayer = player.getKey();
        playCurrentPlayer();
        player.setValue(currentPlayer.getCurrentTile().getId());
        if (!playing) {
          break;
        }
      }
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