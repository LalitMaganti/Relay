package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class ChannelStopEvent extends ChannelEvent {

    public ChannelStopEvent(final Channel channel) {
        super(channel);
    }
}
