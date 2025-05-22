package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;

public class MonopolyBoardFactory {
    private static final int BOARD_SIZE = 20;

    public static Board createBoard() {
        Board board = new Board(BOARD_SIZE);

        // Create and add all tiles
        createAndAddTiles(board);

        // Fill empty positions with blank Tile (no action)
        java.util.stream.IntStream.range(0, BOARD_SIZE).forEach(i -> {
            if (board.getTile(i) == null) {
                board.addTile(new Tile(i));
            }
        });

        // Connect tiles in a circle
        connectTilesCircular(board);

        return board;
    }

    private static void createAndAddTiles(Board board) {
        // GO (top-left corner)
        board.addTile(new GoTile(0));
        // Top row (positions 1-4)
        addPropertyGroup(board, 1, 4, 0, 100, 20);
        // GO TO JAIL (top-right corner)
        board.addTile(new Tile(5, new GoToJailAction(15)));
        // Right column (positions 6-9)
        addPropertyGroup(board, 6, 9, 1, 150, 30);
        // FREE PARKING (bottom-right corner)
        board.addTile(new FreeParkingTile(10));
        // Bottom row (positions 11-14)
        addPropertyGroup(board, 11, 14, 2, 200, 40);
        // JAIL (bottom-left corner)
        board.addTile(new JailTile(15));
        // Left column (positions 16-19)
        addPropertyGroup(board, 16, 19, 3, 250, 50);
    }

    private static void addPropertyGroup(Board board, int startPos, int endPos,
                                       int group, int price, int rent) {
        java.util.stream.IntStream.rangeClosed(startPos, endPos).forEach(pos ->
            board.addTile(new PropertyTile(pos, price, rent, group))
        );
    }

    private static void connectTilesCircular(Board board) {
        // Connect all tiles in a circle
        java.util.stream.IntStream.range(0, BOARD_SIZE - 1).forEach(i ->
            board.connectTiles(i, board.getTile(i + 1))
        );
        // Connect last tile back to first tile
        board.connectTiles(BOARD_SIZE - 1, board.getTile(0));
    }

    // New configuration: 28-tile board
    public static Board createBoard28() {
        final int BOARD_SIZE = 28;
        Board board = new Board(BOARD_SIZE);
        // Place special tiles in corners
        board.addTile(new GoTile(0)); // GO (top-left)
        board.addTile(new Tile(7, new GoToJailAction(21))); // GO TO JAIL (top-right)
        board.addTile(new FreeParkingTile(14)); // FREE PARKING (bottom-right)
        board.addTile(new JailTile(21)); // JAIL (bottom-left)
        // Add property tiles in a new pattern
        addPropertyGroup(board, 1, 3, 0, 100, 20); // Top row
        addPropertyGroup(board, 5, 6, 1, 120, 24); // Top row, before corner
        addPropertyGroup(board, 8, 10, 2, 140, 28); // Right column
        addPropertyGroup(board, 12, 13, 3, 160, 32); // Right column, before corner
        addPropertyGroup(board, 15, 17, 0, 180, 36); // Bottom row
        addPropertyGroup(board, 19, 20, 1, 200, 40); // Bottom row, before corner
        addPropertyGroup(board, 22, 24, 2, 220, 44); // Left column
        addPropertyGroup(board, 26, 27, 3, 240, 48); // Left column, before corner
        // Fill empty positions with blank Tile (no action)
        java.util.stream.IntStream.range(0, BOARD_SIZE).forEach(i -> {
            if (board.getTile(i) == null) {
                board.addTile(new Tile(i));
            }
        });
        // Connect tiles in a circle
        connectTilesCircular(board, BOARD_SIZE);
        return board;
    }

    // New configuration: 32-tile board
    public static Board createBoard32() {
        final int BOARD_SIZE = 32;
        Board board = new Board(BOARD_SIZE);
        // Place special tiles in corners
        board.addTile(new GoTile(0)); // GO (top-left)
        board.addTile(new Tile(8, new GoToJailAction(24))); // GO TO JAIL (top-right)
        board.addTile(new FreeParkingTile(16)); // FREE PARKING (bottom-right)
        board.addTile(new JailTile(24)); // JAIL (bottom-left)
        // Add property tiles in a new pattern
        addPropertyGroup(board, 1, 3, 0, 100, 20); // Top row
        addPropertyGroup(board, 5, 7, 1, 120, 24); // Top row, before corner
        addPropertyGroup(board, 9, 11, 2, 140, 28); // Right column
        addPropertyGroup(board, 13, 15, 3, 160, 32); // Right column, before corner
        addPropertyGroup(board, 17, 19, 0, 180, 36); // Bottom row
        addPropertyGroup(board, 21, 23, 1, 200, 40); // Bottom row, before corner
        addPropertyGroup(board, 25, 27, 2, 220, 44); // Left column
        addPropertyGroup(board, 29, 31, 3, 240, 48); // Left column, before corner
        // Fill empty positions with blank Tile (no action)
        java.util.stream.IntStream.range(0, BOARD_SIZE).forEach(i -> {
            if (board.getTile(i) == null) {
                board.addTile(new Tile(i));
            }
        });
        // Connect tiles in a circle
        connectTilesCircular(board, BOARD_SIZE);
        return board;
    }

    // Overloaded connectTilesCircular for custom board size
    private static void connectTilesCircular(Board board, int boardSize) {
        java.util.stream.IntStream.range(0, boardSize - 1).forEach(i ->
            board.connectTiles(i, board.getTile(i + 1))
        );
        board.connectTiles(boardSize - 1, board.getTile(0));
    }
}
