package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class ModeEvent extends ChannelEvent {

    public final String recipient;

    public final WorldUser sendingUser;

    public final String mode;

    public ModeEvent(final Channel channel, final WorldUser sendingUser, final String recipient,
            final String mode) {
        super(channel);

        this.recipient = recipient;
        this.sendingUser = sendingUser;
        this.mode = mode;
    }
}