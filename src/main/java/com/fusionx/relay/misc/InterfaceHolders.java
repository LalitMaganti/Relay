package com.fusionx.relay.misc;

import com.fusionx.relay.interfaces.EventPreferences;
import com.fusionx.relay.interfaces.EventResponses;

public final class InterfaceHolders {

    private static EventPreferences sPreferences;

    private static EventResponses sEventResponses;

    public static void onInterfaceReceived(final EventPreferences eventPreferences,
            final EventResponses responses) {
        sPreferences = eventPreferences;
        sEventResponses = responses;
    }

    public static EventPreferences getPreferences() {
        return sPreferences;
    }

    public static EventResponses getEventResponses() {
        return sEventResponses;
    }
}