package com.fusionx.relay.event.query;

import com.fusionx.relay.RelayMainUser;
import com.fusionx.relay.QueryUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(QueryUser user, final RelayMainUser relayMainUser, final String message) {
        super(user, relayMainUser);

        this.message = message;
    }
}