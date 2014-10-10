package co.fusionx.relay.core;

public interface Registerable {

    public void registerForEvents(final Object object);

    public void registerForEvents(final Object object, int priority);

    public void unregisterFromEvents(final Object object);
}