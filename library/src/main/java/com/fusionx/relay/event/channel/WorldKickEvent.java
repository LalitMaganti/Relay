package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.nick.Nick;

public class WorldKickEvent extends WorldUserEvent {

    public final Nick kickingNick;

    public final String reason;

    public WorldKickEvent(final Channel channel, final WorldUser kickedUser,
            final WorldUser kickingNick, final String reason) {
        super(channel, kickedUser.getNick());

        this.kickingNick = kickingNick.getNick();
        this.reason = reason;
    }
}