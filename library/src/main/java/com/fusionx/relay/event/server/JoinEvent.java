package com.fusionx.relay.event.server;

import com.fusionx.relay.Channel;

public final class JoinEvent extends ServerEvent {

    public final String channelName;

    public JoinEvent(final Channel channel) {
        channelName = channel.getName();
    }
}