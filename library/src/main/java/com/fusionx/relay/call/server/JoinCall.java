package com.fusionx.relay.call.server;

import com.fusionx.relay.call.Call;

public class JoinCall extends Call {

    private final String channelName;

    public JoinCall(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getLineToSendServer() {
        return "JOIN " + channelName;
    }
}