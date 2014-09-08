package co.fusionx.relay.event.query;

import co.fusionx.relay.base.LibraryUser;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayLibraryUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(final QueryUser user, final LibraryUser libraryUser,
            final String message) {
        super(user, libraryUser);

        this.message = message;
    }
}