package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.event.Event;

public abstract class QueryEvent extends Event {

    public final QueryUser user;

    QueryEvent(final QueryUser user) {
        this.user = user;
    }
}
