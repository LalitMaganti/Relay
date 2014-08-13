package com.fusionx.relay.util;

import com.google.common.base.Optional;

import android.util.Log;

public class LogUtils {

    private static final String TAG = "Relay";

    public static void logOptionalBug(final Optional<?> optional) {
        if (!optional.isPresent()) {
            Log.e(TAG, "Missing an optional which is required - indicates the possible presence "
                    + "of a bug");
        }
    }
}
