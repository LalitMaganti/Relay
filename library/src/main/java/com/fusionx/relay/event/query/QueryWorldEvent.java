package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public abstract class QueryWorldEvent extends QueryEvent {

    QueryWorldEvent(QueryUser user) {
        super(user);
    }
}