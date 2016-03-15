package co.fusionx.relay.event.query;

import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.event.Event;

public abstract class QueryEvent extends Event {

    public final QueryUser user;

    QueryEvent(final QueryUser user) {
        this.user = user;
    }
}
