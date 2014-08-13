package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;

public class ChannelWorldJoinEvent extends ChannelWorldUserEvent {

    public ChannelWorldJoinEvent(final Channel channel, final ChannelUser user) {
        super(channel, user);
    }
}