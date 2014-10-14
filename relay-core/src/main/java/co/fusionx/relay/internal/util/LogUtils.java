package co.fusionx.relay.internal.util;

import co.fusionx.relay.configuration.SessionConfiguration;

public class LogUtils {

    public static void logOptionalBug(final SessionConfiguration configuration) {
        configuration.getDebuggingProvider().logNonFatalError(configuration
                .getConnectionConfiguration().getUrl());
    }
}
