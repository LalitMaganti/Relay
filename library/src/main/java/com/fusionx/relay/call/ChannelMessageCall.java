package com.fusionx.relay.call;

public class ChannelMessageCall extends Call {
    public final String channelName;

    public final String message;

    public ChannelMessageCall(String channelName, String message) {
        this.channelName = channelName;
        this.message = message;
    }
}