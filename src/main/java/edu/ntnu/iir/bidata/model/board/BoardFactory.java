package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.*;
import java.util.List;

public class BoardFactory {

    /**
     * Creates a standard board with the same logic as the old NewBoardGame.initializeBoard().
     * @param boardSize The size of the board.
     * @param players The player list (needed for SwitchPositionAction).
     * @return A fully initialized Board.
     */
    public static Board createStandardBoard(int boardSize, List<edu.ntnu.iir.bidata.model.Player> players) {
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

}