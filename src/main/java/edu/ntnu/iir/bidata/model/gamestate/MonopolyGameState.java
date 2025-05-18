package edu.ntnu.iir.bidata.model.gamestate;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MonopolyGameState {
    private List<PlayerState> players;
    private int currentPlayerIndex;
    private boolean gameStarted;
    private int boardSize;
    private int numberOfDice;
    private String gameType = "MONOPOLY";

    public MonopolyGameState() {
        this.players = new ArrayList<>();
        this.gameType = "MONOPOLY";
    }

    public static class PlayerState {
        private String name;
        private int money;
        private int position;
        private boolean inJail;
        private boolean canLeaveJailNextTurn;
        private List<Integer> ownedPropertyIds;

        public PlayerState() {
            this.ownedPropertyIds = new ArrayList<>();
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getMoney() { return money; }
        public void setMoney(int money) { this.money = money; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
        public boolean isInJail() { return inJail; }
        public void setInJail(boolean inJail) { this.inJail = inJail; }
        public boolean isCanLeaveJailNextTurn() { return canLeaveJailNextTurn; }
        public void setCanLeaveJailNextTurn(boolean canLeaveJailNextTurn) { this.canLeaveJailNextTurn = canLeaveJailNextTurn; }
        public List<Integer> getOwnedPropertyIds() { return ownedPropertyIds; }
        public void setOwnedPropertyIds(List<Integer> ownedPropertyIds) { this.ownedPropertyIds = ownedPropertyIds; }
    }

    // Getters and setters
    public List<PlayerState> getPlayers() { return players; }
    public void setPlayers(List<PlayerState> players) { this.players = players; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex = currentPlayerIndex; }
    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }
    public int getBoardSize() { return boardSize; }
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
    public int getNumberOfDice() { return numberOfDice; }
    public void setNumberOfDice(int numberOfDice) { this.numberOfDice = numberOfDice; }
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }

    // Convert from BoardGame to MonopolyGameState
    public static MonopolyGameState fromBoardGame(BoardGame boardGame) {
        MonopolyGameState state = new MonopolyGameState();
        state.setGameStarted(boardGame.isGameInitialized());
        state.setCurrentPlayerIndex(boardGame.getCurrentPlayerIndex());
        state.setBoardSize(boardGame.getBoard().getSizeOfBoard());
        state.setNumberOfDice(boardGame.getCurrentDiceValues().length);

        for (Player player : boardGame.getPlayers()) {
            if (player instanceof SimpleMonopolyPlayer) {
                SimpleMonopolyPlayer monopolyPlayer = (SimpleMonopolyPlayer) player;
                PlayerState playerState = new PlayerState();
                playerState.setName(monopolyPlayer.getName());
                playerState.setMoney(monopolyPlayer.getMoney());
                playerState.setPosition(monopolyPlayer.getCurrentTile().getId());
                playerState.setInJail(monopolyPlayer.isInJail());
                playerState.setCanLeaveJailNextTurn(monopolyPlayer.isCanLeaveJailNextTurn());
                
                List<Integer> propertyIds = monopolyPlayer.getOwnedProperties().stream()
                    .map(property -> property.getId())
                    .collect(Collectors.toList());
                playerState.setOwnedPropertyIds(propertyIds);
                
                state.getPlayers().add(playerState);
            }
        }
        return state;
    }

    // Convert to BoardGame
    public BoardGame toBoardGame() {
        // Always create a new Monopoly board using the factory
        Board board = MonopolyBoardFactory.createBoard();
        BoardGame boardGame = new BoardGame(board, this.numberOfDice);

        // Re-create players and set their state
        List<Player> players = new ArrayList<>();
        for (PlayerState ps : this.players) {
            SimpleMonopolyPlayer player = new SimpleMonopolyPlayer(ps.getName());
            player.setMoney(ps.getMoney());
            player.setInJail(ps.isInJail());
            // Set player position
            Tile tile = board.getTile(ps.getPosition());
            player.setCurrentTile(tile);

            // Set owned properties
            for (int propId : ps.getOwnedPropertyIds()) {
                Tile t = board.getTile(propId);
                if (t instanceof PropertyTile) {
                    player.getOwnedProperties().add((PropertyTile) t);
                    ((PropertyTile) t).setOwner(player);
                }
            }
            players.add(player);
        }
        boardGame.setPlayers(players);
        boardGame.setCurrentPlayerIndex(this.currentPlayerIndex);
        return boardGame;
    }
} 