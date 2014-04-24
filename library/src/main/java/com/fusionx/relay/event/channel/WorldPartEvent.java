package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class WorldPartEvent extends WorldUserEvent {

    public final String reason;

    public WorldPartEvent(final Channel channel, final WorldUser user, final String reason) {
        super(channel, user.getNick());

        this.reason = reason;
    }
}