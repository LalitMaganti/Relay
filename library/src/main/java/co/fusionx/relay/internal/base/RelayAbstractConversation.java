package co.fusionx.relay.internal.base;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.base.Conversation;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.EventBus;

public abstract class RelayAbstractConversation<T extends Event> implements Conversation<T> {

    protected final Server mServer;

    protected final List<T> mBuffer;

    protected final EventBus<T> mEventBus;

    protected boolean mValid;

    // For RelayServer implementation, the server cane be null - RelayServer MUST override
    // getServer however
    public RelayAbstractConversation(final Server server) {
        mServer = server;
        mBuffer = new ArrayList<>();
        mEventBus = new EventBus<>();
        mValid = true;
    }

    /**
     * Returns the server this channel is attached to
     *
     * @return the server this channel belongs to
     */
    @Override
    public Server getServer() {
        return mServer;
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
     * Gets the buffer of the channel - the events which occured since this channel was created
     *
     * @return a list of the events
     */
    @Override
    public List<T> getBuffer() {
        return mBuffer;
    }

    // Implementation specific methods
    public void postAndStoreEvent(final T event) {
        mBuffer.add(event);

        mEventBus.post(event);
        getServer().getServerWideBus().post(event);
    }

    public void markInvalid() {
        mValid = false;
    }
}