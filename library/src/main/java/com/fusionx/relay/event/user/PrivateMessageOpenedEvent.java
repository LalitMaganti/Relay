package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class PrivateMessageOpenedEvent extends UserEvent {

    public PrivateMessageOpenedEvent(PrivateMessageUser user) {
        super(user);
    }
}
