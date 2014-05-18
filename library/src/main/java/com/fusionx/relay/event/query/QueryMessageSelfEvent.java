package com.fusionx.relay.event.query;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.QueryUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(QueryUser user, final AppUser appUser,
            final String message) {
        super(user, appUser);

        this.message = message;
    }
}