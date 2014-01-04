package com.fusionx.relay.event.server;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class KickEvent extends ServerEvent {

    public final String channelName;

    public final String kickingNick;

    public final String reason;

    public KickEvent(final Channel channel, final WorldUser kickingUser, final String reason) {
        channelName = channel.getName();
        kickingNick = kickingUser.getColorfulNick();
        this.reason = reason;
    }
}