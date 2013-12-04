package com.fusionx.androidirclibrary.misc;

import com.fusionx.androidirclibrary.interfaces.EventPreferences;
import com.fusionx.androidirclibrary.interfaces.EventStringResponses;

public final class InterfaceHolders {

    private static EventPreferences sPreferences;

    private static EventStringResponses sEventResponses;

    public static void onInterfaceReceived(final EventPreferences eventPreferences,
            final EventStringResponses responses) {
        sPreferences = eventPreferences;
        sEventResponses = responses;
    }

    public static EventPreferences getPreferences() {
        return sPreferences;
    }

    public static EventStringResponses getEventResponses() {
        return sEventResponses;
    }
}