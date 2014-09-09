package co.fusionx.relay.event.query;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.conversation.QueryUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;

    public QueryActionSelfEvent(final QueryUser user, final LibraryUser libraryUser,
            final String action) {
        super(user, libraryUser);

        this.action = action;
    }
}