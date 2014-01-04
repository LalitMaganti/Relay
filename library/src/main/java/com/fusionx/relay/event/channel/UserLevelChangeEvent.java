package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.constants.UserLevel;

public class UserLevelChangeEvent extends ChannelEvent {

    public final UserLevel level;

    public final String rawMode;

    public final String nick;

    public final String changingNick;

    public UserLevelChangeEvent(final Channel channel, final String rawMode, final AppUser user,
            final UserLevel level, final String changingNick) {
        super(channel);

        this.rawMode = rawMode;
        this.level = level;
        this.nick = user.getColorfulNick();
        this.changingNick = changingNick;
    }
}