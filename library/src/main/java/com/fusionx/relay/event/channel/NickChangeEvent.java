package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;

public class NickChangeEvent extends ChannelEvent {

    public final String oldNick;

    public final String newNick;

    public NickChangeEvent(final Channel channel, final String oldNick, final AppUser user) {
        super(channel);

        this.oldNick = oldNick;
        this.newNick = user.getColorfulNick();
    }
}