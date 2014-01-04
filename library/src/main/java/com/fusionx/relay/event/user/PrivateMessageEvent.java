package com.fusionx.relay.event.user;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.PrivateMessageUser;

public class PrivateMessageEvent extends PrivateUserEvent {

    public final String message;

    public PrivateMessageEvent(PrivateMessageUser user, final AppUser appUser,
            final String message) {
        super(user, appUser);

        this.message = message;
    }
}