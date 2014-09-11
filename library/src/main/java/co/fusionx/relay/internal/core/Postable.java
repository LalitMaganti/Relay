package co.fusionx.relay.internal.core;

public interface Postable<T> {

    public void postEvent(final T event);
}