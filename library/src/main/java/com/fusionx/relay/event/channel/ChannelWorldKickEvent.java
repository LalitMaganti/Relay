package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Nick;
import com.fusionx.relay.RelayChannelUser;

import java8.util.Optional;

public class ChannelWorldKickEvent extends ChannelWorldUserEvent {

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public ChannelWorldKickEvent(final Channel channel, final ChannelUser kickedUser,
            final Optional<RelayChannelUser> optKickingUser, final String kickingNickString,
            final String reason) {
        super(channel, kickedUser.getNick());

        this.kickingNick = optKickingUser.map(ChannelUser::getNick).orElse(null);
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}