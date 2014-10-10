package co.fusionx.relay.internal.bus;

public class DefaultBus<T> implements EventBus<T> {

    private final com.google.common.eventbus.EventBus mBus;

    public DefaultBus() {
        mBus = new com.google.common.eventbus.EventBus();
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
    }
}