package com.fusionx.relay.event.server;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.nick.Nick;

public class KickEvent extends ServerEvent {

    public final String channelName;

    public final Nick kickingNick;

    public final String reason;

    public KickEvent(final Channel channel, final ChannelUser kickingUser, final String reason) {
        channelName = channel.getName();
        kickingNick = kickingUser.getNick();
        this.reason = reason;
    }
}