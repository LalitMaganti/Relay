package com.fusionx.relay.call;

public class ChannelKickCall extends Call {

    public final String channelName;

    public final String userNick;

    public final String reason;

    public ChannelKickCall(final String channelName, final String userNick, final String reason) {
        this.channelName = channelName;
        this.userNick = userNick;
        this.reason = reason;
    }
}