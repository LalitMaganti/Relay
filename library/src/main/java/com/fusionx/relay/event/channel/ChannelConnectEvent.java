package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class ChannelConnectEvent extends ChannelEvent {

    public ChannelConnectEvent(final Channel channel) {
        super(channel);
    }
}