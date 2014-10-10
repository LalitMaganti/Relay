package co.fusionx.relay.event.query;

import co.fusionx.relay.conversation.QueryUser;

public class QueryDisconnectEvent extends QueryEvent {

    public final String message;

    public QueryDisconnectEvent(final QueryUser user, final String message) {
        super(user);
        this.message = message;
    }
}