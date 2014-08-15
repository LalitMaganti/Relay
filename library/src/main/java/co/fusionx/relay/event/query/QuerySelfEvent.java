package co.fusionx.relay.event.query;

import co.fusionx.relay.RelayMainUser;
import co.fusionx.relay.QueryUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final RelayMainUser ourUser;

    QuerySelfEvent(final QueryUser user, final RelayMainUser relayMainUser) {
        super(user);

        this.ourUser = relayMainUser;
    }
}