package com.fusionx.relay.misc;

import com.fusionx.relay.interfaces.RelayConfiguration;

public final class RelayConfigurationProvider {

    private static RelayConfiguration sPreferences;

    public static void onInterfaceReceived(final RelayConfiguration relayConfiguration) {
        sPreferences = relayConfiguration;
    }

    public static RelayConfiguration getPreferences() {
        return sPreferences;
    }
}