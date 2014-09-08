package co.fusionx.relay.event.server;

import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.base.Server;

public class NewPrivateMessageEvent extends ServerEvent {

    public final QueryUser user;

    public NewPrivateMessageEvent(final Server server, final QueryUser user) {
        super(server);

        this.user = user;
    }
}