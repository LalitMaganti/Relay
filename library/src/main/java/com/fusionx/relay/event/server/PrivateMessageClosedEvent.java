package com.fusionx.relay.event.server;

import com.fusionx.relay.PrivateMessageUser;

public class PrivateMessageClosedEvent extends ServerEvent {

    public final String privateMessageNick;

    public PrivateMessageClosedEvent(final PrivateMessageUser user) {
        privateMessageNick = user.getNick();
    }
}
