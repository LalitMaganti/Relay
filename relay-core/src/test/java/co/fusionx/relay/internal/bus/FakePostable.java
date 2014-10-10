package co.fusionx.relay.internal.bus;

import co.fusionx.relay.internal.core.Postable;

public class FakePostable<T> implements Postable<T> {

    private T mLastEvent;

    @Override
    public void postEvent(final T event) {
        mLastEvent = event;
    }

    public T lastEvent() {
        return mLastEvent;
    }
}