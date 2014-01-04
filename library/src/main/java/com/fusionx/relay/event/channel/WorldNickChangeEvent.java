package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class WorldNickChangeEvent extends WorldUserEvent {

    public final String oldNick;

    public WorldNickChangeEvent(final Channel channel, final String oldNick, final WorldUser user) {
        super(channel, user);

        this.oldNick = oldNick;
    }
}