package co.fusionx.relay.event.query;

import co.fusionx.relay.conversation.QueryUser;

public class QueryOpenedEvent extends QueryEvent {

    public QueryOpenedEvent(QueryUser user) {
        super(user);
    }
}
