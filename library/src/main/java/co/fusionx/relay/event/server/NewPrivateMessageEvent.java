package co.fusionx.relay.event.server;

import co.fusionx.relay.QueryUser;

public class NewPrivateMessageEvent extends ServerEvent {

    public final QueryUser user;

    public NewPrivateMessageEvent(final QueryUser user) {
        super(user.getServer());

        this.user = user;
    }
}