package edu.ntnu.iir.bidata.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages game events and observer notifications.
 */
public class GameEventManager {
    private final List<BoardGameObserver> observers;
    private final List<GameEvent> eventQueue;

    public GameEventManager() {
        this.observers = new ArrayList<>();
        this.eventQueue = new ArrayList<>();
    }

    public void addObserver(BoardGameObserver observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        observers.add(observer);
    }

    public void removeObserver(BoardGameObserver observer) {
        observers.remove(observer);
    }

    public void notifyPlayerMoved(Player player, int newPosition) {
        GameEvent event = new GameEvent(GameEventType.PLAYER_MOVED, player, newPosition);
        queueEvent(event);
    }

    public void notifyGameWon(Player winner) {
        GameEvent event = new GameEvent(GameEventType.GAME_WON, winner);
        queueEvent(event);
    }

    public void notifyTurnChanged(Player currentPlayer) {
        GameEvent event = new GameEvent(GameEventType.TURN_CHANGED, currentPlayer);
        queueEvent(event);
    }

    private void queueEvent(GameEvent event) {
        eventQueue.add(event);
        processEvents();
    }

    private void processEvents() {
        while (!eventQueue.isEmpty()) {
            GameEvent event = eventQueue.remove(0);
            dispatchEvent(event);
        }
    }

    private void dispatchEvent(GameEvent event) {
        for (BoardGameObserver observer : observers) {
            switch (event.getType()) {
                case PLAYER_MOVED:
                    observer.onPlayerMoved(event.getPlayer(), event.getPosition());
                    break;
                case GAME_WON:
                    observer.onGameWon(event.getPlayer());
                    break;
                case TURN_CHANGED:
                    observer.onTurnChanged(event.getPlayer());
                    break;
            }
        }
    }
} 