package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public abstract class WorldUserEvent extends ChannelEvent {

    public final String nick;

    WorldUserEvent(final Channel channel, final WorldUser user) {
        this(channel, user != null ? user.getPrettyNick(channel) : null);
    }

    WorldUserEvent(final Channel channel, final String nick) {
        super(channel);

        // NICK should never be null
        if (nick != null) {
            this.nick = nick;
        } else {
            throw new NullPointerException();
        }
    }
}