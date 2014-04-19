package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class UserStopEvent extends UserEvent {

    public UserStopEvent(final PrivateMessageUser user) {
        super(user);
    }
}
