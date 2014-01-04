package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class WorldJoinEvent extends WorldUserEvent {

    public WorldJoinEvent(final Channel channel, final WorldUser user) {
        super(channel, user);
    }
}