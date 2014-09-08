package co.fusionx.relay.bus;

public interface GenericBus<T> {

    public void register(final Object object);

    public void register(final Object object, int priority);

    public void unregister(final Object object);

    public void post(final T event);
}