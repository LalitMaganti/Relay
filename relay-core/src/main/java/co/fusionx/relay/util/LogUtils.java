package co.fusionx.relay.util;

import co.fusionx.relay.configuration.SessionConfiguration;

public class LogUtils {

    public static void logOptionalBug(final SessionConfiguration configuration) {
        configuration.getSettingsProvider().logNonFatalError(configuration
                .getConnectionConfiguration().getUrl());
    }
}
