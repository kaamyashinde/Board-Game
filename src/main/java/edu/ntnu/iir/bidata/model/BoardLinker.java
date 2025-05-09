package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for linking tiles on the board and validating the board structure.
 */
public class BoardLinker {
    private final Map<Integer, Tile> tiles;
    private final int boardSize;

    public BoardLinker(Map<Integer, Tile> tiles, int boardSize) {
        this.tiles = tiles;
        this.boardSize = boardSize;
    }

    public void linkTiles() {
        validateBoardSize();
        linkAdjacentTiles();
        validateBoardStructure();
    }

    private void validateBoardSize() {
        ParameterValidation.validateNonZeroPositiveInteger(boardSize, "board size");
        if (tiles.size() != boardSize) {
            throw new IllegalArgumentException("Number of tiles does not match board size");
        }
    }

    private void linkAdjacentTiles() {
        for (int i = 0; i < boardSize - 1; i++) {
            Tile currentTile = tiles.get(i);
            Tile nextTile = tiles.get(i + 1);
            currentTile.setNextTile(nextTile);
        }
    }

    private void validateBoardStructure() {
        // Check for circular references
        Map<Integer, Boolean> visited = new HashMap<>();
        for (int i = 0; i < boardSize; i++) {
            visited.put(i, false);
        }

        int currentPosition = 0;
        while (currentPosition < boardSize) {
            if (visited.get(currentPosition)) {
                throw new IllegalStateException("Circular reference detected in board structure");
            }
            visited.put(currentPosition, true);
            Tile currentTile = tiles.get(currentPosition);
            if (currentTile.getNextTile() == null && currentPosition != boardSize - 1) {
                throw new IllegalStateException("Invalid board structure: missing link at position " + currentPosition);
            }
            currentPosition++;
        }
    }
} 