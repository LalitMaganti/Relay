package co.fusionx.relay.internal.bus;

import co.fusionx.relay.bus.GenericBus;

public interface PostableBus<T> extends GenericBus {

    public void postEvent(final T event);
}