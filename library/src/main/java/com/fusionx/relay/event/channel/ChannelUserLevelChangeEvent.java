package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.constants.UserLevel;

public class ChannelUserLevelChangeEvent extends ChannelEvent {

    public final UserLevel level;

    public final String rawMode;

    public final AppUser user;

    public final ChannelUser changingUser;

    public final String changingNick;

    public ChannelUserLevelChangeEvent(final Channel channel, final String rawMode,
            final AppUser user,
            final UserLevel level, final ChannelUser changingUser, String changingNick) {
        super(channel);

        this.rawMode = rawMode;
        this.level = level;
        this.user = user;
        this.changingUser = changingUser;
        this.changingNick = changingNick;
    }
}