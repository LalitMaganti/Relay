package co.fusionx.relay.internal.function;

public interface Consumer<T> {

    public void apply(final T object);
}