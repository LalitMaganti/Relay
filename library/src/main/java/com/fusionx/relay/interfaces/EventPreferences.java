package com.fusionx.relay.interfaces;

public interface EventPreferences {

    public int getReconnectAttemptsCount();

    public String getPartReason();

    public String getQuitReason();

    public boolean isSelfEventHidden();

    public boolean isMOTDShown();
}