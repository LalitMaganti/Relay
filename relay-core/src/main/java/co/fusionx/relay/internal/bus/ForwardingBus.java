package co.fusionx.relay.internal.bus;

import co.fusionx.relay.internal.core.Postable;

public class ForwardingBus<T> implements EventBus<T> {

    private final com.google.common.eventbus.EventBus mBus;

    private final Postable<? super T> mForwardBus;

    public ForwardingBus(final com.google.common.eventbus.EventBus bus,
            final Postable<? super T> forwardBus) {
        mBus = bus;
        mForwardBus = forwardBus;
    }

    @Override
    public void registerForEvents(final Object object) {
        mBus.register(object);
    }

    @Override
    public void registerForEvents(final Object object, final int priority) {
        // mBus.register(object, priority);
    }

    @Override
    public void unregisterFromEvents(final Object object) {
        mBus.unregister(object);
    }

    @Override
    public void postEvent(final T event) {
        mBus.post(event);
        mForwardBus.postEvent(event);
    }
}
