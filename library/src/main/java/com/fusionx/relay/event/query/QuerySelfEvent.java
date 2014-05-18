package com.fusionx.relay.event.query;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.QueryUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final AppUser ourUser;

    QuerySelfEvent(final QueryUser user, final AppUser appUser) {
        super(user);

        this.ourUser = appUser;
    }
}