package com.fusionx.relay.event.user;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.PrivateMessageUser;

public class ClosePrivateUserEvent extends PrivateUserEvent {

    public ClosePrivateUserEvent(final PrivateMessageUser user, final AppUser appUser) {
        super(user, appUser);
    }
}
