package co.fusionx.relay.internal.function;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public class Optionals {

    public static <T> void ifPresent(final Optional<T> optional, final Consumer<T> consumer) {
        if (optional.isPresent()) {
            consumer.apply(optional.get());
        }
    }

    public static <T, U> Optional<? extends U> flatTransform(final Optional<T> optional,
            final Function<T, Optional<? extends U>> function) {
        if (optional.isPresent()) {
            return function.apply(optional.get());
        }
        return Optional.absent();
    }
}