package com.fusionx.relay.event.user;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.PrivateMessageUser;

public class PrivateActionEvent extends PrivateUserEvent {

    public final String action;

    public PrivateActionEvent(final PrivateMessageUser user, final AppUser appUser,
            final String action) {
        super(user, appUser);

        this.action = action;
    }
}