package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class WorldPrivateQuitEvent extends WorldPrivateEvent {

    public WorldPrivateQuitEvent(final PrivateMessageUser user) {
        super(user);
    }
}