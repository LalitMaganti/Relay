package com.fusionx.relay.event;

import com.fusionx.relay.constants.UserListChangeType;

public class MessageEvent extends ChannelEvent {

    public MessageEvent(String channelName, String message) {
        super(channelName, message, UserListChangeType.NONE, null);
    }
}
