package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryQuitWorldEvent extends QueryWorldEvent {

    public QueryQuitWorldEvent(final QueryUser user) {
        super(user);
    }
}