package com.fusionx.relay.event.user;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.PrivateMessageUser;

public class PrivateUserEvent extends UserEvent {

    public final String appUserNick;

    public PrivateUserEvent(final PrivateMessageUser user, AppUser appUser) {
        super(user);

        this.appUserNick = appUser.getColorfulNick();
    }
}