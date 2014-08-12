package com.fusionx.relay.util;

import android.util.Log;

import java8.util.Optional;

public class LogUtils {

    private static final String TAG = "Relay";

    public static void logOptionalBug(final Optional<?> optional) {
        if (!optional.isPresent()) {
            Log.e(TAG, "Missing an optional which is required - indicates the possible presence "
                    + "of a bug");
        }
    }
}
