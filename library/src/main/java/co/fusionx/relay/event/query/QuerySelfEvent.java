package co.fusionx.relay.event.query;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.conversation.QueryUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final LibraryUser libraryUser;

    QuerySelfEvent(final QueryUser user, final LibraryUser libraryUser) {
        super(user);

        this.libraryUser = libraryUser;
    }
}