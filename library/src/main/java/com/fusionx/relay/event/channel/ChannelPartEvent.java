package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class ChannelPartEvent extends ChannelEvent {

    public ChannelPartEvent(Channel channel) {
        super(channel.getName());
    }
}
