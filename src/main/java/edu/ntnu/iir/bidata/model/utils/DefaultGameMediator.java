package edu.ntnu.iir.bidata.model.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import edu.ntnu.iir.bidata.Inject;

public class DefaultGameMediator implements GameMediator {
    private final Set<GameMediatorListener> listeners = new CopyOnWriteArraySet<>();

    @Inject
    public DefaultGameMediator() {}

    public void register(GameMediatorListener listener) {
        listeners.add(listener);
    }

    public void unregister(GameMediatorListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notify(Object sender, String event) {
        for (GameMediatorListener listener : listeners) {
            listener.onEvent(sender, event);
        }
    }

    public interface GameMediatorListener {
        void onEvent(Object sender, String event);
    }
} 