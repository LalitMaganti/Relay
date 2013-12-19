package com.fusionx.relay.event;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.constants.UserListChangeType;

public class ChannelEvent extends Event {

    public final String channelName;

    public final String message;

    public final UserListChangeType changeType;

    public final ChannelUser user;

    public ChannelEvent(final String channelName, final String message,
            final UserListChangeType changeType, final ChannelUser user) {
        this.channelName = channelName;
        this.message = message;
        this.changeType = changeType;
        this.user = user;
    }
}