package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryConnectEvent extends QueryEvent {

    public QueryConnectEvent(QueryUser user) {
        super(user);
    }
}