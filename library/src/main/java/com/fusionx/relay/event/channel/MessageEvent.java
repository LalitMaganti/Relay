package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class MessageEvent extends ChannelEvent {

    public final String message;

    public final String nick;

    public MessageEvent(final Channel channel, final String message, String nick) {
        super(channel);

        this.message = message;
        this.nick = nick;
    }
}