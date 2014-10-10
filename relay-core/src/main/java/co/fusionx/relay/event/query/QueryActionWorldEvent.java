package co.fusionx.relay.event.query;

import co.fusionx.relay.conversation.QueryUser;

public class QueryActionWorldEvent extends QueryWorldEvent {

    public final String action;

    public QueryActionWorldEvent(QueryUser userNick, String action) {
        super(userNick);

        this.action = action;
    }
}