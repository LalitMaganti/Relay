package com.fusionx.relay.event.query;

import com.fusionx.relay.RelayMainUser;
import com.fusionx.relay.QueryUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;

    public QueryActionSelfEvent(final QueryUser user, final RelayMainUser relayMainUser, final String action) {
        super(user, relayMainUser);

        this.action = action;
    }
}