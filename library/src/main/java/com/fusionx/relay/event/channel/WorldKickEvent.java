package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class WorldKickEvent extends WorldUserEvent {

    public final String kickingNick;

    public final String reason;

    public WorldKickEvent(final Channel channel, final WorldUser kickedUser,
            final WorldUser kickingNick, final String reason) {
        super(channel, kickedUser);

        this.kickingNick = kickingNick.getPrettyNick(channel);
        this.reason = reason;
    }
}