package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.event.Event;

public class UserEvent extends Event {

    public final PrivateMessageUser user;

    public UserEvent(final PrivateMessageUser user) {
        this.user = user;
    }
}
