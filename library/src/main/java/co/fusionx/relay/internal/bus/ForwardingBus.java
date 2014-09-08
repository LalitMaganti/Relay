package co.fusionx.relay.internal.bus;

import com.fusionx.bus.Bus;

import co.fusionx.relay.bus.GenericBus;

public class ForwardingBus<T> implements GenericBus<T> {

    private final Bus mBus;

    private final GenericBus<? super T> mForwardBus;

    public ForwardingBus(final Bus bus, final GenericBus<? super T> forwardBus) {
        mBus = bus;
        mForwardBus = forwardBus;
    }

    @Override
    public void register(final Object object) {
        mBus.register(object);
    }

    @Override
    public void register(final Object object, final int priority) {
        mBus.register(object, priority);
    }

    @Override
    public void unregister(final Object object) {
        mBus.unregister(object);
    }

    @Override
    public void post(final T event) {
        mBus.post(event);
        mForwardBus.post(event);
    }
}
