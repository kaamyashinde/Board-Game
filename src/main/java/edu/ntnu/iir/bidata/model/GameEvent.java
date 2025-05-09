package edu.ntnu.iir.bidata.model;

import lombok.Getter;

/**
 * Represents a game event with its associated data.
 */
@Getter
public class GameEvent {
    private final GameEventType type;
    private final Player player;
    private final int position;

    public GameEvent(GameEventType type, Player player) {
        this(type, player, -1);
    }

    public GameEvent(GameEventType type, Player player, int position) {
        this.type = type;
        this.player = player;
        this.position = position;
    }
} 