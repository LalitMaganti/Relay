package co.fusionx.relay.event.query;

import co.fusionx.relay.QueryUser;
import co.fusionx.relay.RelayMainUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final RelayMainUser ourUser;

    QuerySelfEvent(final QueryUser user, final RelayMainUser relayMainUser) {
        super(user);

        this.ourUser = relayMainUser;
    }
}