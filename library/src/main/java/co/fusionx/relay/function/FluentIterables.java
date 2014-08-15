package co.fusionx.relay.function;

import com.google.common.collect.FluentIterable;

public class FluentIterables {

    public static <T> void forEach(final FluentIterable<T> fluentIterable,
            final Consumer<T> consumer) {
        for (final T object : fluentIterable) {
            consumer.apply(object);
        }
    }
}