package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;

public class ChannelWorldPartEvent extends ChannelWorldUserEvent {

    public final String reason;

    public ChannelWorldPartEvent(final Channel channel, final ChannelUser user, final String reason) {
        super(channel, user.getNick());

        this.reason = reason;
    }
}