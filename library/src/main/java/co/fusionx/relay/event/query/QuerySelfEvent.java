package co.fusionx.relay.event.query;

import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayLibraryUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final RelayLibraryUser ourUser;

    QuerySelfEvent(final QueryUser user, final RelayLibraryUser relayLibraryUser) {
        super(user);

        this.ourUser = relayLibraryUser;
    }
}