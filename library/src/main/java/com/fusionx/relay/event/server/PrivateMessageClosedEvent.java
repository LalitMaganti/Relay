package com.fusionx.relay.event.server;

import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.nick.Nick;

public class PrivateMessageClosedEvent extends ServerEvent {

    public final Nick privateMessageNick;

    public PrivateMessageClosedEvent(final PrivateMessageUser user) {
        privateMessageNick = user.getNick();
    }
}
