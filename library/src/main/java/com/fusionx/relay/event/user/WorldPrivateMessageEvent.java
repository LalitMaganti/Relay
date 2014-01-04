package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class WorldPrivateMessageEvent extends WorldPrivateEvent {

    public final String message;

    public WorldPrivateMessageEvent(final PrivateMessageUser user, final String message) {
        super(user);

        this.message = message;
    }
}