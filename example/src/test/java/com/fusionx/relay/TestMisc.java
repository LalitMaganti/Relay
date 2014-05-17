package com.fusionx.relay;

import com.fusionx.relay.interfaces.EventPreferences;

public class TestMisc {

    public static class DefaultEventPreferences implements EventPreferences {

        @Override
        public int getReconnectAttemptsCount() {
            return 0;
        }

        @Override
        public String getPartReason() {
            return null;
        }

        @Override
        public String getQuitReason() {
            return null;
        }

        @Override
        public boolean isSelfEventBroadcast() {
            return false;
        }

        @Override
        public boolean isMOTDShown() {
            return false;
        }
    }
}
