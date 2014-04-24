package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

/**
 * Both user and nick can be null
 */
public class WorldMessageEvent extends WorldUserEvent {

    public final String message;

    public WorldMessageEvent(final Channel channel, final String message,
            final WorldUser sendingUser, final boolean mention) {
        super(channel, sendingUser, mention);

        this.message = message;
    }

    public WorldMessageEvent(Channel channel, String message, String sendingNick, boolean mention) {
        super(channel, sendingNick, mention);

        this.message = message;
    }
}
