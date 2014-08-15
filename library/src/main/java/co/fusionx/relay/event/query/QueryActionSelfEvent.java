package co.fusionx.relay.event.query;

import co.fusionx.relay.RelayMainUser;
import co.fusionx.relay.QueryUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;

    public QueryActionSelfEvent(final QueryUser user, final RelayMainUser relayMainUser, final String action) {
        super(user, relayMainUser);

        this.action = action;
    }
}