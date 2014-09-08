package co.fusionx.relay.event.query;

import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayLibraryUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(final QueryUser user, final RelayLibraryUser relayLibraryUser,
            final String message) {
        super(user, relayLibraryUser);

        this.message = message;
    }
}