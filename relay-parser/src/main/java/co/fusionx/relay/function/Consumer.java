package co.fusionx.relay.function;

public interface Consumer<T> {

    public void apply(final T object);
}