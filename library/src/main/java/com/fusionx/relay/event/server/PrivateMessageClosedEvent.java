package com.fusionx.relay.event.server;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.nick.Nick;

public class PrivateMessageClosedEvent extends ServerEvent {

    public final Nick privateMessageNick;

    public PrivateMessageClosedEvent(final QueryUser user) {
        privateMessageNick = user.getNick();
    }
}
