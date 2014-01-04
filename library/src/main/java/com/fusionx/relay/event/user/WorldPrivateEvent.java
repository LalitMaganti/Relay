package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class WorldPrivateEvent extends UserEvent {

    public WorldPrivateEvent(PrivateMessageUser user) {
        super(user);
    }
}