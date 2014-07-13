package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;

public class ChannelModeEvent extends ChannelEvent {

    public final String recipient;

    public final ChannelUser sendingUser;

    public final String sendingNick;

    public final String mode;

    public ChannelModeEvent(final Channel channel, final ChannelUser sendingUser, String sendingNick,
            final String recipient, final String mode) {
        super(channel);

        this.recipient = recipient;
        this.sendingUser = sendingUser;
        this.sendingNick = sendingNick;
        this.mode = mode;
    }
}