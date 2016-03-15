package co.fusionx.relay.base;

import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.sender.QuerySender;

public interface QueryUser extends Conversation<QueryEvent>, QuerySender {

    // Nick delegates
    public Nick getNick();
}
