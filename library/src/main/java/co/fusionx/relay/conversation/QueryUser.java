package co.fusionx.relay.conversation;

import co.fusionx.relay.core.Nick;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.sender.QuerySender;

public interface QueryUser extends Conversation<QueryEvent>, QuerySender {

    // Nick delegates
    public Nick getNick();
}