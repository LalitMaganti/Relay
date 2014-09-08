package co.fusionx.relay.internal.base;

import com.fusionx.bus.Bus;

import java.util.List;

import co.fusionx.relay.base.Conversation;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.bus.BufferingBus;
import co.fusionx.relay.internal.bus.ConversationBus;
import co.fusionx.relay.internal.bus.ForwardingBus;

public abstract class RelayAbstractConversation<T extends Event> implements Conversation<T> {

    private final BufferingBus<T> mBus;

    private final GenericBus<Event> mSessionBus;

    private boolean mValid;

    public RelayAbstractConversation(final GenericBus<Event> sessionBus) {
        mSessionBus = sessionBus;

        mBus = new BufferingBus<>(new ForwardingBus<>(new Bus(), sessionBus));
        mValid = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericBus<T> getBus() {
        return mBus;
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
        return mBus.getBuffer();
    }

    @Override
    public GenericBus<Event> getSessionBus() {
        return mSessionBus;
    }

    public void markInvalid() {
        mValid = false;
    }
}