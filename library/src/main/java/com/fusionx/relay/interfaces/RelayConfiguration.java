package com.fusionx.relay.interfaces;

import com.fusionx.relay.Server;

public interface RelayConfiguration {

    public int getReconnectAttemptsCount();

    public String getPartReason();

    public String getQuitReason();

    public boolean isSelfEventHidden();

    public boolean isMOTDShown();

    public void logMissingData(final Server server);

    public void logServerLine(final String line);
}