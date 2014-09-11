package co.fusionx.relay.internal.bus;

import co.fusionx.relay.bus.GenericBus;

public class FakeEventBus<T> implements GenericBus<T> {

    private T mLastEvent;

    @Override
    public void registerForEvents(final Object object) {
        // This is fake
    }

    @Override
    public void registerForEvents(final Object object, final int priority) {
        // This is fake
    }

    @Override
    public void unregisterFromEvents(final Object object) {
        // This is fake
    }

    @Override
    public void post(final T event) {
        mLastEvent = event;
    }

    public T lastEvent() {
        return mLastEvent;
    }
}