package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.constants.UserLevel;

public class ChannelWorldLevelChangeEvent extends ChannelWorldUserEvent {

    public final String rawMode;

    public final ChannelUser changingUser;

    public final String changingNick;

    public final UserLevel level;

    public ChannelWorldLevelChangeEvent(final Channel channel, String rawMode,
            final ChannelUser user,
            final UserLevel level, final ChannelUser changingUser, String changingNick) {
        super(channel, user);

        this.rawMode = rawMode;
        this.level = level;
        this.changingUser = changingUser;
        this.changingNick = changingNick;
    }
}