package co.fusionx.relay.event.query;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.conversation.QueryUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;

    public QueryMessageSelfEvent(final QueryUser user, final LibraryUser libraryUser,
            final String message) {
        super(user, libraryUser);

        this.message = message;
    }
}