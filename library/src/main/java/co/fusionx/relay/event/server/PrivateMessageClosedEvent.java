package co.fusionx.relay.event.server;

import co.fusionx.relay.Nick;
import co.fusionx.relay.QueryUser;

public class PrivateMessageClosedEvent extends ServerEvent {

    public final Nick privateMessageNick;

    public PrivateMessageClosedEvent(final QueryUser user) {
        super(user.getServer());

        privateMessageNick = user.getNick();
    }
}
