package co.fusionx.relay.event.query;

import co.fusionx.relay.QueryUser;

public class QueryNoSuchNickEvent extends QueryWorldEvent {

    public final String message;

    public QueryNoSuchNickEvent(final QueryUser user, String message) {
        super(user);

        this.message = message;
    }
}