package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryActionWorldEvent extends QueryWorldEvent {

    public final String action;

    public QueryActionWorldEvent(QueryUser userNick, String action) {
        super(userNick);

        this.action = action;
    }
}