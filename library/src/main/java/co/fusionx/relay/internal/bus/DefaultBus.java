package co.fusionx.relay.internal.bus;

import com.fusionx.bus.Bus;

public class DefaultBus<T> implements PostableBus<T> {

    private final Bus mBus;

    public DefaultBus() {
        mBus = new Bus();
    }

    @Override
    public void registerForEvents(final Object object) {
        mBus.register(object);
    }

    @Override
    public void registerForEvents(final Object object, final int priority) {
        mBus.register(object, priority);
    }

    @Override
    public void unregisterFromEvents(final Object object) {
        mBus.unregister(object);
    }

    @Override
    public void postEvent(final T event) {
        mBus.post(event);
    }
}