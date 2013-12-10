package com.fusionx.androidirclibrary.interfaces;

import com.fusionx.androidirclibrary.constants.Theme;

public interface EventPreferences {

    public int getReconnectAttemptsCount();

    public String getPartReason();

    public String getQuitReason();

    public boolean getShouldTimestampMessages();

    public Theme getTheme();

    public boolean shouldIgnoreUser(final String nick);

    public boolean shouldLogUserListChanges();

    public boolean shouldSendSelfMessageEvent();
}