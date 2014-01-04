package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class WorldMessageEvent extends WorldUserEvent {

    public final String message;

    public WorldMessageEvent(final Channel channel, final String message,
            final WorldUser sendingUser, final String nick) {
        super(channel, sendingUser == null ? nick : sendingUser.getPrettyNick(channel));

        this.message = message;
    }
}
