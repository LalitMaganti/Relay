package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.nick.Nick;

public class NickChangeEvent extends ChannelEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public final AppUser appUser;

    public NickChangeEvent(final Channel channel, final Nick oldNick, final AppUser user) {
        super(channel);

        this.oldNick = oldNick;
        this.newNick = user.getNick();
        this.appUser = user;
    }
}