package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;

public class QueryNoSuchNickWorldEvent extends QueryWorldEvent {

    public final String message;

    public QueryNoSuchNickWorldEvent(final QueryUser user, String message) {
        super(user);

        this.message = message;
    }
}