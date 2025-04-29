package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages the game state and transitions.
 */
public class GameStateManager {
    private final Map<Player, Integer> players;
    private Player currentPlayer;
    private boolean playing;
    private Player winner;

    public GameStateManager(Map<Player, Integer> players) {
        this.players = players;
        this.playing = false;
        this.winner = null;
    }

    public void initializeGame() {
        ParameterValidation.validatePlayersExist(players);
        ParameterValidation.validateGameNotStarted(playing);
        
        currentPlayer = players.keySet().iterator().next();
        playing = true;
    }

    public void nextPlayer() {
        List<Player> playerList = new ArrayList<>(players.keySet());
        int currentIndex = playerList.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % playerList.size();
        currentPlayer = playerList.get(nextIndex);
    }

    public void setWinner(Player player) {
        this.winner = player;
        this.playing = false;
    }

    public boolean isGameOver() {
        return !playing;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isPlaying() {
        return playing;
    }

    public Player getWinner() {
        return winner;
    }
} 