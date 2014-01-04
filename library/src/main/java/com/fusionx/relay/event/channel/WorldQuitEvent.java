package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class WorldQuitEvent extends WorldUserEvent {

    public final String reason;

    public WorldQuitEvent(final Channel channel, final WorldUser user, final String reason) {
        super(channel, user);

        this.reason = reason;
    }
}