package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.event.Event;

public abstract class ChannelEvent extends Event {

    public final Channel channel;

    public final String channelName;

    ChannelEvent(final Channel channel) {
        this.channel = channel;
        this.channelName = channel.getName();
    }

    ChannelEvent(final String channelName) {
        this.channel = null;
        this.channelName = channelName;
    }
}