package co.fusionx.relay.event.query;

import co.fusionx.relay.conversation.QueryUser;

public class QueryStopEvent extends QueryEvent {

    public QueryStopEvent(final QueryUser user) {
        super(user);
    }
}
