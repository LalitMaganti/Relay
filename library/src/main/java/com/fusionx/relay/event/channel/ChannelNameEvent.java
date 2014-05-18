package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

import java.util.Collection;

public class ChannelNameEvent extends ChannelEvent {

    public final Collection<WorldUser> users;

    public ChannelNameEvent(final Channel channel, final Collection<WorldUser> users) {
        super(channel);

        this.users = users;
    }
}
