package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryMessageWorldEvent extends QueryWorldEvent {

    public final String message;

    public QueryMessageWorldEvent(final QueryUser user, final String message) {
        super(user);

        this.message = message;
    }
}