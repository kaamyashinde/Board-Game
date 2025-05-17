package edu.ntnu.iir.bidata.model.game;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.player.Player;

import java.time.LocalDateTime;
import java.util.List;

public class GameState {
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private String gameName;

    public GameState(Board board, List<Player> players, int currentPlayerIndex, String gameName) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.gameName = gameName;
    }

    // Getters
    public Board getBoard() { return board; }
    public List<Player> getPlayers() { return players; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public String getGameName() { return gameName; }

    // Setters
    public void setBoard(Board board) { this.board = board; }
    public void setPlayers(List<Player> players) { this.players = players; }
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex = currentPlayerIndex; }
    public void setGameName(String gameName) { this.gameName = gameName; }
} 