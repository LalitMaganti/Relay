package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class ModeEvent extends ChannelEvent {

    public final String recipient;

    public final WorldUser sendingUser;

    public final String sendingNick;

    public final String mode;

    public ModeEvent(final Channel channel, final WorldUser sendingUser, String sendingNick,
            final String recipient, final String mode) {
        super(channel);

        this.recipient = recipient;
        this.sendingUser = sendingUser;
        this.sendingNick = sendingNick;
        this.mode = mode;
    }
}