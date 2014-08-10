package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.constants.UserLevel;

import java8.util.Optional;

public class ChannelUserLevelChangeEvent extends ChannelEvent {

    public final UserLevel level;

    public final String rawMode;

    public final AppUser user;

    public final Optional<? extends ChannelUser> changingUser;

    public final String changingNick;

    public ChannelUserLevelChangeEvent(final Channel channel, final String rawMode,
            final AppUser user, final UserLevel level,
            final Optional<? extends ChannelUser> changingUser,
            final String changingNick) {
        super(channel);

        this.rawMode = rawMode;
        this.level = level;
        this.user = user;
        this.changingUser = changingUser;
        this.changingNick = changingNick;
    }
}