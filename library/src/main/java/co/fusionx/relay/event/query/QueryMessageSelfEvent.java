package co.fusionx.relay.event.query;

import co.fusionx.relay.QueryUser;
import co.fusionx.relay.RelayMainUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(final QueryUser user, final RelayMainUser relayMainUser,
            final String message) {
        super(user, relayMainUser);

        this.message = message;
    }
}