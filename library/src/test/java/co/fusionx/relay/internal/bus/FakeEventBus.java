package co.fusionx.relay.internal.bus;

public class FakeEventBus<T> extends FakePostable<T> implements EventBus<T> {

    @Override
    public void registerForEvents(final Object object) {
        // This is fake - do nothing
    }

    @Override
    public void registerForEvents(final Object object, final int priority) {
        // This is fake - do nothing
    }

    @Override
    public void unregisterFromEvents(final Object object) {
        // This is fake - do nothing
    }
}