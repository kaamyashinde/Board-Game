package edu.ntnu.iir.bidata.model.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import edu.ntnu.iir.bidata.Inject;

/**
 * Default implementation of the {@link GameMediator} interface.
 * Maintains a set of listeners and notifies them about events.
 */
public class DefaultGameMediator implements GameMediator {

    private final Set<GameMediatorListener> listeners = new CopyOnWriteArraySet<>();

    /**
     * Constructs a new {@code DefaultGameMediator} instance.
     */
    @Inject
    public DefaultGameMediator() {}

    /**
     * Registers a new {@link GameMediatorListener} to receive event notifications.
     *
     * @param listener the listener to register
     */
    public void register(GameMediatorListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a previously registered {@link GameMediatorListener}.
     *
     * @param listener the listener to remove
     */
    public void unregister(GameMediatorListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners of a specific event.
     *
     * @param sender the source of the event
     * @param event the name or description of the event
     */
    @Override
    public void notify(Object sender, String event) {
        listeners.forEach(listener -> listener.onEvent(sender, event));
    }

    /**
     * Listener interface for receiving game mediator events.
     */
    public interface GameMediatorListener {

        /**
         * Invoked when an event occurs.
         *
         * @param sender the source of the event
         * @param event the name or description of the event
         */
        void onEvent(Object sender, String event);
    }
}
