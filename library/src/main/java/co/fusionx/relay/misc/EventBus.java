package co.fusionx.relay.misc;

import com.fusionx.bus.Bus;

public class EventBus {

    private final Bus mBus;

    public EventBus() {
        mBus = new Bus();
    }

    public void register(final Object object) {
        mBus.register(object);
    }

    public void register(final Object object, final int priority) {
        mBus.register(object, priority);
    }

    public void unregister(final Object object) {
        mBus.unregister(object);
    }

    public void post(final Object event) {
        mBus.post(event);
    }
}