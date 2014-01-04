package com.fusionx.relay.interfaces;

import com.fusionx.relay.constants.Theme;

public interface EventPreferences {

    public int getReconnectAttemptsCount();

    public String getPartReason();

    public String getQuitReason();

    public Theme getTheme();

    public boolean shouldIgnoreUser(final String nick);

    public boolean shouldLogUserListChanges();

    public boolean isSelfEventBroadcast();

    public boolean isMOTDShown();

    public boolean shouldHighlightLine();

    public boolean shouldNickBeColourful();
}