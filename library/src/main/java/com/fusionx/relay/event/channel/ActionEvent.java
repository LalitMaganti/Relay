package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class ActionEvent extends ChannelEvent {

    public final String action;

    public final String nick;

    public ActionEvent(final Channel channel, String action, String nick) {
        super(channel);

        this.action = action;
        this.nick = nick;
    }
}