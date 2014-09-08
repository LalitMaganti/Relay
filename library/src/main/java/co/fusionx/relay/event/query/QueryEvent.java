package co.fusionx.relay.event.query;

import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.event.Event;

public abstract class QueryEvent extends Event<QueryUser, QueryEvent> {

    QueryEvent(final QueryUser user) {
        super(user);
    }
}
