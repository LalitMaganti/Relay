package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.event.Event;

public class ChannelEvent extends Event {

    public final String channelName;

    public ChannelEvent(final Channel channel) {
        this.channelName = channel.getName();
    }
}