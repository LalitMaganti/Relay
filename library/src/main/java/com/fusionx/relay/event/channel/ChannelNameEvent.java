package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;

import java.util.Collection;

public class ChannelNameEvent extends ChannelEvent {

    public final Collection<ChannelUser> users;

    public ChannelNameEvent(final Channel channel, final Collection<ChannelUser> users) {
        super(channel);

        this.users = users;
    }
}
