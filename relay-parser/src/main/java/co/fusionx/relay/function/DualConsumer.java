package co.fusionx.relay.function;

public interface DualConsumer<T, U> {

    public void apply(final T first, final U second);
}