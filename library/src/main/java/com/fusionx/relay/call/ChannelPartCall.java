package com.fusionx.relay.call;

public class ChannelPartCall extends Call {

    public final String channelName;

    public final String reason;

    public ChannelPartCall(final String channelName, final String reason) {
        this.channelName = channelName;
        this.reason = reason;
    }
}