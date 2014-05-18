package com.fusionx.relay.event.query;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.QueryUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;

    public QueryActionSelfEvent(final QueryUser user, final AppUser appUser,
            final String action) {
        super(user, appUser);

        this.action = action;
    }
}