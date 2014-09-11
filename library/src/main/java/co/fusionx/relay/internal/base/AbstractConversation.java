package co.fusionx.relay.internal.base;

import com.fusionx.bus.Bus;

import java.util.List;

import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.bus.BufferingBus;
import co.fusionx.relay.internal.bus.ForwardingBus;
import co.fusionx.relay.internal.core.InternalConversation;
import co.fusionx.relay.internal.core.Postable;

public abstract class AbstractConversation<T extends Event> implements InternalConversation<T> {

    private final BufferingBus<T> mBus;

    private boolean mValid;

    public AbstractConversation(final Postable<Event> sessionBus) {
        mBus = new BufferingBus<>(new ForwardingBus<>(new Bus(), sessionBus));
        mValid = true;
    }

    @Override
    public List<T> getBuffer() {
        return mBus.getBuffer();
    }

    @Override
    public void registerForEvents(final Object object) {
        mBus.registerForEvents(object);
    }

    @Override
    public void registerForEvents(final Object object, final int priority) {
        mBus.registerForEvents(object, priority);
    }

    @Override
    public void unregisterFromEvents(final Object object) {
        mBus.unregisterFromEvents(object);
    }

    @Override
    public void postEvent(final T event) {
        mBus.postEvent(event);
    }

    @Override
    public boolean isValid() {
        return mValid;
    }

    @Override
    public void markInvalid() {
        mValid = false;
    }
}