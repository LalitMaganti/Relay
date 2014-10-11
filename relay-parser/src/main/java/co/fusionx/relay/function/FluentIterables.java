package co.fusionx.relay.function;

import com.google.common.collect.FluentIterable;

public class FluentIterables {

    public static <T> void forEach(final FluentIterable<T> iterable, final Consumer<T> consumer) {
        for (T item : iterable) {
            consumer.apply(item);
        }
    }
}