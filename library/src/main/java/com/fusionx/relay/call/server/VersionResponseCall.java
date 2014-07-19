package com.fusionx.relay.call.server;

import com.fusionx.relay.call.Call;

public class VersionResponseCall extends Call {

    private final String mRecipient;

    public VersionResponseCall(final String askingUser) {
        mRecipient = askingUser;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001VERSION :%s\u0001", mRecipient, "Relay:1.0:Android");
    }
}