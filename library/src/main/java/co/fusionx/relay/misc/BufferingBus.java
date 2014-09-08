package co.fusionx.relay.misc;

import java.util.ArrayList;
import java.util.List;

public class BufferingBus<T> implements GenericBus<T> {

    private final List<T> mBuffer;

    private final GenericBus<T> mBus;

    public BufferingBus(final GenericBus<T> bus) {
        mBus = bus;

        mBuffer = new ArrayList<>();
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
        mBuffer.add(event);
    }

    public List<T> getBuffer() {
        return mBuffer;
    }
}
