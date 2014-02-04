package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class UserConnectEvent extends UserEvent {

    public UserConnectEvent(PrivateMessageUser user) {
        super(user);
    }
}