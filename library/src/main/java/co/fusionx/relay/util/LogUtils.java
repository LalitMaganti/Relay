package co.fusionx.relay.util;

import com.google.common.base.Optional;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class LogUtils {

    private static final String TAG = "Relay";

    public static void logOptionalBug(final Optional<?> optional, final Server server) {
        if (!optional.isPresent()) {
            RelayConfigurationProvider.getPreferences().logMissingData(server);
        }
    }
}
