package co.fusionx.relay.event.query;

import co.fusionx.relay.conversation.QueryUser;

public class QueryQuitWorldEvent extends QueryWorldEvent {

    public QueryQuitWorldEvent(final QueryUser user) {
        super(user);
    }
}