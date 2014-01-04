package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class NoSuchNickEvent extends WorldPrivateEvent {

    public final String message;

    public NoSuchNickEvent(final PrivateMessageUser user, String message) {
        super(user);

        this.message = message;
    }
}