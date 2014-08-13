package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryNoSuchNickEvent extends QueryWorldEvent {

    public final String message;

    public QueryNoSuchNickEvent(final QueryUser user, String message) {
        super(user);

        this.message = message;
    }
}