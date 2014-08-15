package co.fusionx.relay.function;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public class Optionals {

    public static <T> void ifPresent(final Optional<T> optional, final Consumer<T> consumer) {
        if (optional.isPresent()) {
            consumer.apply(optional.get());
        }
    }

    public static <T, U> Optional<U> flatTransform(final Optional<T> optional, final Function<T,
            Optional<U>> function) {
        return optional.isPresent() ? function.apply(optional.get()) : Optional.absent();
    }
}