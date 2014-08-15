package co.fusionx.relay.event.query;

import co.fusionx.relay.QueryUser;

public class QueryOpenedEvent extends QueryEvent {

    public QueryOpenedEvent(QueryUser user) {
        super(user);
    }
}
