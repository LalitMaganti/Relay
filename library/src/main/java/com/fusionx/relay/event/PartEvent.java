package com.fusionx.relay.event;

/**
 * Event which is sent when the user parts from a channel and this has been recognized by the
 * server
 *
 * CAUTION - it is unsafe to access the channel which was parted at the time this event is sent -
 * it no longer exists in the UserChannelInterface
 */
public class PartEvent extends Event {

    public final String channelName;

    public final String reason;

    public PartEvent(final String reason) {
        this.channelName = reason;
        this.reason = "";
    }

    public PartEvent(final String channelName, final String reason) {
        this.channelName = channelName;
        this.reason = reason;
    }
}