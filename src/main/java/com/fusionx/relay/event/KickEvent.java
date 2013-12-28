package com.fusionx.relay.event;

public class KickEvent extends Event {

    public final String channelName;

    public final String userNick;

    public final String reason;

    public KickEvent(final String channelName, final String userNick, final String reason) {
        this.channelName = channelName;
        this.userNick = userNick;
        this.reason = reason;
    }
}