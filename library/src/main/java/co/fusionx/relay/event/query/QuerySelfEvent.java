package co.fusionx.relay.event.query;

import co.fusionx.relay.base.LibraryUser;
import co.fusionx.relay.base.QueryUser;

public abstract class QuerySelfEvent extends QueryEvent {

    public final LibraryUser libraryUser;

    QuerySelfEvent(final QueryUser user, final LibraryUser libraryUser) {
        super(user);

        this.libraryUser = libraryUser;
    }
}