package co.fusionx.relay.event.query;

import co.fusionx.relay.QueryUser;

public abstract class QueryWorldEvent extends QueryEvent {

    QueryWorldEvent(QueryUser user) {
        super(user);
    }
}