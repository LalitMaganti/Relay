package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryOpenedEvent extends QueryEvent {

    public QueryOpenedEvent(QueryUser user) {
        super(user);
    }
}
