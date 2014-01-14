package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class ModeEvent extends ChannelEvent {

    public final String recipient;

    public final String sendingUserNick;

    public final String mode;

    public ModeEvent(final Channel channel, final String sendingUserNick, final String recipient,
            final String mode) {
        super(channel);

        this.recipient = recipient;
        this.sendingUserNick = sendingUserNick;
        this.mode = mode;
    }
}