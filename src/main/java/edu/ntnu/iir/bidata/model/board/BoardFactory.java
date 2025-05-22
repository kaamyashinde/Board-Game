package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.actions.base.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.actions.base.SafeSpotAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.SwitchPositionAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.EntryPointAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.core.TileFactory;
import java.util.List;

/**
 * A utility class for creating various types of game boards such as standard boards, Snakes and
 * Ladders boards, and Ludo boards.
 */
public class BoardFactory {

  /**
   * Creates a standard board with the same logic as the old NewBoardGame.initializeBoard().
   *
   * @param boardSize The size of the board.
   * @param players The player list (needed for SwitchPositionAction).
   * @return A fully initialized Board.
   */
  public static Board createStandardBoard(int boardSize, List<Player> players) {
    Board board = new Board(boardSize);

    // Add tiles and actions
    java.util.stream.IntStream.range(0, board.getSizeOfBoard())
        .forEach(
            i -> {
              TileAction action = null;
              if (i == 3) {
                action = new HopFiveStepsAction();
              } else if (i == 7) {
                action = new GoToTileAction(12);
              } else if (i == 5) {
                action = new LoseTurnAction();
              } else if (i == 15) {
                action = new SwitchPositionAction(players);
              }
              if (!board.addTile(i, action)) {
                throw new GameException("Failed to add tile at position " + i);
              }
            });

    // Connect tiles
    java.util.stream.IntStream.range(0, board.getSizeOfBoard() - 1)
        .forEach(i -> board.connectTiles(i, board.getTile(i + 1)));

    // Validate connections
    java.util.stream.IntStream.range(0, board.getSizeOfBoard() - 1)
        .forEach(
            i -> {
              if (!board.isValidTileConnection(i, i + 1)) {
                throw new GameException(
                    "Invalid tile connection between tiles " + i + " and " + (i + 1));
              }
            });

    return board;
  }

  /**
   * Creates a Snakes and Ladders board using TileFactory and a default TileConfiguration.
   *
   * @param boardSize The size of the board (typically 100 for Snakes and Ladders).
   * @param players The player list.
   * @return A fully initialized Snakes and Ladders Board.
   */
  public static Board createSnakesAndLaddersBoard(int boardSize, List<Player> players) {
    return createSnakesAndLaddersBoard(boardSize, players, new TileConfiguration());
  }

  /**
   * Creates a Snakes and Ladders board using TileFactory and TileConfiguration.
   *
   * @param boardSize The size of the board (typically 100 for Snakes and Ladders).
   * @param players The player list.
   * @param config The TileConfiguration for the board.
   * @return A fully initialized Snakes and Ladders Board.
   */
  public static Board createSnakesAndLaddersBoard(
      int boardSize, List<Player> players, TileConfiguration config) {
    Board board = new Board(boardSize);
    TileFactory tileFactory = new TileFactory(players, config);

    // Add tiles with snakes, ladders, etc.
    java.util.stream.IntStream.range(0, board.getSizeOfBoard())
        .forEach(
            i -> {
              if (!board.addTile(i, tileFactory.createTile(i).getAction())) {
                throw new GameException("Failed to add tile at position " + i);
              }
            });
    // Connect tiles
    java.util.stream.IntStream.range(0, board.getSizeOfBoard() - 1)
        .forEach(i -> board.connectTiles(i, board.getTile(i + 1)));
    // Validate connections
    java.util.stream.IntStream.range(0, board.getSizeOfBoard() - 1)
        .forEach(
            i -> {
              if (!board.isValidTileConnection(i, i + 1)) {
                throw new GameException(
                    "Invalid tile connection between tiles " + i + " and " + (i + 1));
              }
            });
    return board;
  }

  /**
   * Creates a Ludo board with the standard 52-tile layout including home areas, safe spots, and
   * entry points for each player.
   *
   * @param players The list of players (2-4 players supported).
   * @return A fully initialized Ludo Board.
   * @throws GameException if invalid number of players or board creation fails.
   */
  public static Board createLudoBoard(List<Player> players) {
    if (players.size() < 2 || players.size() > 4) {
      throw new GameException("Ludo requires 2-4 players");
    }

    // Standard Ludo board has 52 tiles in the main track
    final int MAIN_TRACK_SIZE = 52;
    Board board = new Board(MAIN_TRACK_SIZE);

    // Add tiles with their respective actions
    java.util.stream.IntStream.range(0, MAIN_TRACK_SIZE)
        .forEach(
            i -> {
              TileAction action = null;

              // Safe spots (every 13th tile)
              if (i % 13 == 0) {
                action = new SafeSpotAction();
              }

              // Entry points for each player
              if (i == 0) { // Red player entry
                action = new EntryPointAction(players.get(0));
              } else if (i == 13) { // Green player entry
                action = new EntryPointAction(players.get(1));
              } else if (i == 26 && players.size() > 2) { // Yellow player entry
                action = new EntryPointAction(players.get(2));
              } else if (i == 39 && players.size() > 3) { // Blue player entry
                action = new EntryPointAction(players.get(3));
              }

              if (!board.addTile(i, action)) {
                throw new GameException("Failed to add tile at position " + i);
              }
            });

    // Connect tiles in the main track
    java.util.stream.IntStream.range(0, MAIN_TRACK_SIZE - 1)
        .forEach(i -> board.connectTiles(i, board.getTile(i + 1)));
    // Connect last tile to first tile to complete the loop
    board.connectTiles(MAIN_TRACK_SIZE - 1, board.getTile(0));

    // Validate connections
    java.util.stream.IntStream.range(0, MAIN_TRACK_SIZE)
        .forEach(
            i -> {
              int nextTile = (i + 1) % MAIN_TRACK_SIZE;
              if (!board.isValidTileConnection(i, nextTile)) {
                throw new GameException(
                    "Invalid tile connection between tiles " + i + " and " + nextTile);
              }
            });

    return board;
  }
}
