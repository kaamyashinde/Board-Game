package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;

/**
 * Manages game rules and validation.
 */
public class GameRulesManager {
    private final int minPlayers;
    private final int maxPlayers;
    private final int minDice;
    private final int maxDice;
    private final int minBoardSize;
    private final int maxBoardSize;

    public GameRulesManager() {
        this.minPlayers = 2;
        this.maxPlayers = 4;
        this.minDice = 1;
        this.maxDice = 3;
        this.minBoardSize = 20;
        this.maxBoardSize = 100;
    }

    public void validateGameParameters(int numOfDice, int numOfPlayers, int boardSize) {
        validateDiceCount(numOfDice);
        validatePlayerCount(numOfPlayers);
        validateBoardSize(boardSize);
    }

    private void validateDiceCount(int numOfDice) {
        if (numOfDice < minDice || numOfDice > maxDice) {
            throw new GameException("Number of dice must be between " + minDice + " and " + maxDice);
        }
    }

    private void validatePlayerCount(int numOfPlayers) {
        if (numOfPlayers < minPlayers || numOfPlayers > maxPlayers) {
            throw new GameException("Number of players must be between " + minPlayers + " and " + maxPlayers);
        }
    }

    private void validateBoardSize(int boardSize) {
        if (boardSize < minBoardSize || boardSize > maxBoardSize) {
            throw new GameException("Board size must be between " + minBoardSize + " and " + maxBoardSize);
        }
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinDice() {
        return minDice;
    }

    public int getMaxDice() {
        return maxDice;
    }

    public int getMinBoardSize() {
        return minBoardSize;
    }

    public int getMaxBoardSize() {
        return maxBoardSize;
    }
} 