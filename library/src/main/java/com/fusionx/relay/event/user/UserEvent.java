package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.event.Event;

public abstract class UserEvent extends Event {

    public final PrivateMessageUser user;

    UserEvent(final PrivateMessageUser user) {
        this.user = user;
    }
}
