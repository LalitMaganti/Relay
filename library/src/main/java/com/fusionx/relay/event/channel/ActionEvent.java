package com.fusionx.relay.event.channel;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;

public class ActionEvent extends ChannelEvent {

    public final String action;

    public final AppUser user;

    public ActionEvent(final Channel channel, final String action, final AppUser user) {
        super(channel);

        this.action = action;
        this.user = user;
    }
}