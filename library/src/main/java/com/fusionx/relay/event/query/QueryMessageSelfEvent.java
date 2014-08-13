package com.fusionx.relay.event.query;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.RelayMainUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(final QueryUser user, final RelayMainUser relayMainUser,
            final String message) {
        super(user, relayMainUser);

        this.message = message;
    }
}