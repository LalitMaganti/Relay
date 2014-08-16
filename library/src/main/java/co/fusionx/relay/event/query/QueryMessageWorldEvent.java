package co.fusionx.relay.event.query;

import co.fusionx.relay.QueryUser;

public class QueryMessageWorldEvent extends QueryWorldEvent {

    public final String message;

    public QueryMessageWorldEvent(final QueryUser user, final String message) {
        super(user);

        this.message = message;
    }
}