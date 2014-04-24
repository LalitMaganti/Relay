package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.event.Event;

public abstract class ChannelEvent extends Event {

    public final Channel channel;

    ChannelEvent(final Channel channel) {
        this.channel = channel;
    }
}