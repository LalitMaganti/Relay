package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;

public class ChannelMessageEvent extends ChannelEvent {

    public final String message;

    public final AppUser user;

    public ChannelMessageEvent(final Channel channel, final String message, final AppUser user) {
        super(channel);

        this.message = message;
        this.user = user;
    }
}