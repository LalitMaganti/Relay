package com.fusionx.relay.event.user;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.PrivateMessageUser;

public abstract class PrivateUserEvent extends UserEvent {

    public final AppUser ourUser;

    PrivateUserEvent(final PrivateMessageUser user, final AppUser appUser) {
        super(user);

        this.ourUser = appUser;
    }
}