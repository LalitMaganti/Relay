package co.fusionx.relay.internal.bus;

import java.util.ArrayList;
import java.util.List;

public class BufferingBus<T> implements PostableBus<T> {

    private final List<T> mBuffer;

    private final PostableBus<T> mBus;

    public BufferingBus(final PostableBus<T> bus) {
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
