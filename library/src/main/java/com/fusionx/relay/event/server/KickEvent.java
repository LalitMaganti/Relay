package com.fusionx.relay.event.server;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Nick;
import com.fusionx.relay.RelayChannelUser;

import java8.util.Optional;

public class KickEvent extends ServerEvent {

    public final String channelName;

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public KickEvent(final Channel channel, final Optional<RelayChannelUser> optKickingUser,
            final String kickingNickString, final String reason) {
        this.channelName = channel.getName();
        this.kickingNick = optKickingUser.map(ChannelUser::getNick).orElse(null);
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}