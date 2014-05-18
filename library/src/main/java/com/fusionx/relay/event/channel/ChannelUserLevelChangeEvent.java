package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.constants.UserLevel;

public class ChannelUserLevelChangeEvent extends ChannelEvent {

    public final UserLevel level;

    public final String rawMode;

    public final AppUser user;

    public final WorldUser changingUser;

    public ChannelUserLevelChangeEvent(final Channel channel, final String rawMode,
            final AppUser user,
            final UserLevel level, final WorldUser changingUser) {
        super(channel);

        this.rawMode = rawMode;
        this.level = level;
        this.user = user;
        this.changingUser = changingUser;
    }
}