package com.fusionx.relay.event;

import com.fusionx.relay.constants.UserListChangeType;

public class ActionEvent extends ChannelEvent {

    public ActionEvent(String channelName, String message) {
        super(channelName, message, UserListChangeType.NONE, null);
    }
}