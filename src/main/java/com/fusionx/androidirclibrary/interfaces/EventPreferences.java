package com.fusionx.androidirclibrary.interfaces;

import com.fusionx.androidirclibrary.constants.Theme;

public interface EventPreferences {

    public int getReconnectAttemptsCount();

    public String getQuitReason();

    public boolean getShouldTimestampMessages();

    public Theme getTheme();
}