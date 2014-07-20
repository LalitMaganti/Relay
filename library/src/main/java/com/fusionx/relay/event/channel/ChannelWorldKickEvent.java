package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Nick;

public class ChannelWorldKickEvent extends ChannelWorldUserEvent {

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public ChannelWorldKickEvent(final Channel channel, final ChannelUser kickedUser,
            final ChannelUser kickingUser, final String kickingNickString, final String reason) {
        super(channel, kickedUser.getNick());

        this.kickingNick = kickingUser == null ? null : kickingUser.getNick();
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}