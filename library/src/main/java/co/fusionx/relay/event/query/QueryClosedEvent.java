package co.fusionx.relay.event.query;

import co.fusionx.relay.base.QueryUser;

public class QueryClosedEvent extends QueryEvent {

    public QueryClosedEvent(final QueryUser user) {
        super(user);
    }
}
