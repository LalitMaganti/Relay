package co.fusionx.relay.internal.base;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.base.Conversation;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.EventBus;

public abstract class RelayAbstractConversation<T extends Event> implements Conversation<T> {

    private final List<T> mBuffer;

    private final EventBus<T> mEventBus;

    private final EventBus<Event> mConnectionWideEventBus;

    private boolean mValid;

    public RelayAbstractConversation(final EventBus<Event> eventBus) {
        mConnectionWideEventBus = eventBus;

        mBuffer = new ArrayList<>();
        mEventBus = new EventBus<>();
        mValid = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus<T> getBus() {
        return mEventBus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return mValid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getBuffer() {
        return mBuffer;
    }

    @Override
    public EventBus<Event> getConnectionWideBus() {
        return mConnectionWideEventBus;
    }

    // Implementation specific methods
    public void postAndStoreEvent(final T event) {
        mBuffer.add(event);

        mEventBus.post(event);
        mConnectionWideEventBus.post(event);
    }

    public void markInvalid() {
        mValid = false;
    }
}