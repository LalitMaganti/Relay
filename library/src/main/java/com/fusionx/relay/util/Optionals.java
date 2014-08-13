package com.fusionx.relay.util;

import com.google.common.base.Optional;

import com.fusionx.relay.function.Consumer;

public class Optionals {

    public static <T> void ifPresent(final Optional<T> optional, final Consumer<T> function) {
        if (optional.isPresent()) {
            function.apply(optional.get());
        }
    }
}