package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class ChannelModeEvent extends ChannelEvent {

    public final String recipient;

    public final WorldUser sendingUser;

    public final String mode;

    public ChannelModeEvent(final Channel channel, final WorldUser sendingUser,
            final String recipient,
            final String mode) {
        super(channel);

        this.recipient = recipient;
        this.sendingUser = sendingUser;
        this.mode = mode;
    }
}