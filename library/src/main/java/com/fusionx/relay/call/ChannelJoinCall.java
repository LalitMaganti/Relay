package com.fusionx.relay.call;

public class ChannelJoinCall extends Call {

    public final String channelName;

    public ChannelJoinCall(String channelName) {
        this.channelName = channelName;
    }
}