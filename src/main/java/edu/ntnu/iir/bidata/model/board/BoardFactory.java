package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.*;
import java.util.List;
import edu.ntnu.iir.bidata.model.Player;
public class BoardFactory {

    /**
     * Creates a standard board with the same logic as the old NewBoardGame.initializeBoard().
     * @param boardSize The size of the board.
     * @param players The player list (needed for SwitchPositionAction).
     * @return A fully initialized Board.
     */
    public static Board createStandardBoard(int boardSize, List<Player> players) {
        Board board = new Board(boardSize);

        // Add tiles and actions
        for (int i = 0; i < board.getSizeOfBoard(); i++) {
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
        }

        // Connect tiles
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            board.connectTiles(i, board.getTile(i + 1));
        }

        // Validate connections
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            if (!board.isValidTileConnection(i, i + 1)) {
                throw new GameException("Invalid tile connection between tiles " + i + " and " + (i + 1));
            }
        }

        return board;
    }

    /**
     * Creates a Snakes and Ladders board using TileFactory and TileConfiguration.
     * @param boardSize The size of the board (typically 100 for Snakes and Ladders).
     * @param players The player list.
     * @return A fully initialized Snakes and Ladders Board.
     */
    public static Board createSnakesAndLaddersBoard(int boardSize, List<Player> players) {
        Board board = new Board(boardSize);
        TileConfiguration config = new TileConfiguration();
        TileFactory tileFactory = new TileFactory(players, config);

        // Add tiles with snakes, ladders, etc.
        for (int i = 0; i < board.getSizeOfBoard(); i++) {
            if (!board.addTile(i, tileFactory.createTile(i).getAction())) {
                throw new GameException("Failed to add tile at position " + i);
            }
        }
        // Connect tiles
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            board.connectTiles(i, board.getTile(i + 1));
        }
        // Validate connections
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            if (!board.isValidTileConnection(i, i + 1)) {
                throw new GameException("Invalid tile connection between tiles " + i + " and " + (i + 1));
            }
        }
        return board;
    }

    /**
     * Creates a Ludo board. For now, this is a simple linear board with no special actions.
     * @param boardSize The size of the board (can be 52 or 56 for classic Ludo, or any value for now).
     * @param players The player list.
     * @return A fully initialized Ludo Board.
     */
    public static Board createLudoBoard(int boardSize, List<Player> players) {
        Board board = new Board(boardSize);
        // Add plain tiles (no special actions for now)
        for (int i = 0; i < board.getSizeOfBoard(); i++) {
            if (!board.addTile(i, null)) {
                throw new GameException("Failed to add tile at position " + i);
            }
        }
        // Connect tiles
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            board.connectTiles(i, board.getTile(i + 1));
        }
        // Validate connections
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            if (!board.isValidTileConnection(i, i + 1)) {
                throw new GameException("Invalid tile connection between tiles " + i + " and " + (i + 1));
            }
        }
        return board;
    }

}