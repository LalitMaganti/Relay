package co.fusionx.relay.event.query;

import co.fusionx.relay.base.LibraryUser;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayLibraryUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;

    public QueryActionSelfEvent(final QueryUser user, final LibraryUser libraryUser,
            final String action) {
        super(user, libraryUser);

        this.action = action;
    }
}