package co.fusionx.relay.event.query;

import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayMainUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;

    public QueryActionSelfEvent(final QueryUser user, final RelayMainUser relayMainUser,
            final String action) {
        super(user, relayMainUser);

        this.action = action;
    }
}