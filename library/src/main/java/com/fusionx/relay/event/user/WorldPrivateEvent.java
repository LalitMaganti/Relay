package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public abstract class WorldPrivateEvent extends UserEvent {

    WorldPrivateEvent(PrivateMessageUser user) {
        super(user);
    }
}