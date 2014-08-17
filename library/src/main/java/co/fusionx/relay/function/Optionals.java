package co.fusionx.relay.function;

import com.google.common.base.Optional;

public class Optionals {

    public static <T> void ifPresent(final Optional<T> optional, final Consumer<T> consumer) {
        if (optional.isPresent()) {
            consumer.apply(optional.get());
        }
    }
}