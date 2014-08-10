package com.fusionx.relay.event.query;

import com.fusionx.relay.RelayMainUser;
import com.fusionx.relay.QueryUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final RelayMainUser ourUser;

    QuerySelfEvent(final QueryUser user, final RelayMainUser relayMainUser) {
        super(user);

        this.ourUser = relayMainUser;
    }
}