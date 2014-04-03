package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class UserDisconnectEvent extends UserEvent {

    public final String message;

    public UserDisconnectEvent(final PrivateMessageUser user, final String message) {
        super(user);
        this.message = message;
    }
}