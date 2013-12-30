package com.fusionx.relay.event;

/**
 * Event which is sent when the user is kicked from a channel
 *
 * CAUTION - it is unsafe to access the channel which the user is kicked from at the time this
 * event is sent - it no longer exists in the UserChannelInterface
 */
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