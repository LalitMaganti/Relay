package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryStopEvent extends QueryEvent {

    public QueryStopEvent(final QueryUser user) {
        super(user);
    }
}
