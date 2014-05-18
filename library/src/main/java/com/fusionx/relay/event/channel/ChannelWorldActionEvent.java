package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class ChannelWorldActionEvent extends ChannelWorldUserEvent {

    public final String action;

    public ChannelWorldActionEvent(final Channel channel, final String action,
            final WorldUser sendingUser, final boolean mention) {
        super(channel, sendingUser, mention);

        this.action = action;
    }

    public ChannelWorldActionEvent(final Channel channel, final String action,
            final String sendingUser, final boolean mention) {
        super(channel, sendingUser, mention);

        this.action = action;
    }
}