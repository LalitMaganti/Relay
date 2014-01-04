package com.fusionx.relay.misc;

import com.fusionx.relay.interfaces.EventPreferences;

public final class InterfaceHolders {

    private static EventPreferences sPreferences;

    public static void onInterfaceReceived(final EventPreferences eventPreferences) {
        sPreferences = eventPreferences;
    }

    public static EventPreferences getPreferences() {
        return sPreferences;
    }
}