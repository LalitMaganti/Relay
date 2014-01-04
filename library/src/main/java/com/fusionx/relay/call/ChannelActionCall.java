package com.fusionx.relay.call;

public class ChannelActionCall extends Call {

    public final String action;

    public final String channelName;

    public ChannelActionCall(final String channelName, final String action) {
        this.action = action;
        this.channelName = channelName;
    }
}