package com.fusionx.relay.event.server;

import com.fusionx.relay.QueryUser;

public class NewPrivateMessageEvent extends ServerEvent {

    public final QueryUser user;

    public NewPrivateMessageEvent(final QueryUser user) {
        this.user = user;
    }
}