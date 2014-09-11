package co.fusionx.relay.bus;

public interface GenericBus {

    public void registerForEvents(final Object object);

    public void registerForEvents(final Object object, int priority);

    public void unregisterFromEvents(final Object object);
}