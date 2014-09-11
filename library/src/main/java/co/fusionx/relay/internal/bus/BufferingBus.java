package co.fusionx.relay.internal.bus;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.internal.core.Postable;

public class BufferingBus<T> implements EventBus<T> {

    private final List<T> mBuffer;

    private final EventBus<T> mBus;

    public BufferingBus(final EventBus<T> bus) {
        mBus = bus;

        mBuffer = new ArrayList<>();
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
        mBuffer.add(event);
    }

    public List<T> getBuffer() {
        return mBuffer;
    }
}
