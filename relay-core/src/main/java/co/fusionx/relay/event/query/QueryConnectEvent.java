package co.fusionx.relay.event.query;

import co.fusionx.relay.conversation.QueryUser;

public class QueryConnectEvent extends QueryEvent {

    public QueryConnectEvent(final QueryUser user) {
        super(user);
    }
}