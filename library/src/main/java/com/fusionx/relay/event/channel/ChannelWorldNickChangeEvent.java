package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Nick;

public class ChannelWorldNickChangeEvent extends ChannelWorldUserEvent {

    public final Nick oldNick;

    public ChannelWorldNickChangeEvent(final Channel channel, final Nick oldNick,
            final ChannelUser user) {
        super(channel, user);

        this.oldNick = oldNick;
    }
}