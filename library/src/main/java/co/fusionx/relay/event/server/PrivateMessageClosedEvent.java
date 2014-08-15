package co.fusionx.relay.event.server;

import co.fusionx.relay.QueryUser;
import co.fusionx.relay.Nick;

public class PrivateMessageClosedEvent extends ServerEvent {

    public final Nick privateMessageNick;

    public PrivateMessageClosedEvent(final QueryUser user) {
        privateMessageNick = user.getNick();
    }
}
