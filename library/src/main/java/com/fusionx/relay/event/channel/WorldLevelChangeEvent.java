package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.constants.UserLevel;

public class WorldLevelChangeEvent extends WorldUserEvent {

    public final String rawMode;

    public final String changingNick;

    private final UserLevel level;

    public WorldLevelChangeEvent(final Channel channel, String rawMode, final WorldUser user,
            final UserLevel level, final String changingNick) {
        super(channel, user);

        this.rawMode = rawMode;
        this.level = level;
        this.changingNick = changingNick;
    }
}