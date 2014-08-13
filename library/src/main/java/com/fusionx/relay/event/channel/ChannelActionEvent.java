package com.fusionx.relay.event.channel;

import com.fusionx.relay.RelayMainUser;
import com.fusionx.relay.Channel;

public class ChannelActionEvent extends ChannelEvent {

    public final String action;

    public final RelayMainUser user;

    public ChannelActionEvent(final Channel channel, final String action, final RelayMainUser user) {
        super(channel);

        this.action = action;
        this.user = user;
    }
}