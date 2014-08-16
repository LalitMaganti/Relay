package co.fusionx.relay;

import java.util.List;

import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.sender.QuerySender;

public interface QueryUser extends Conversation, QuerySender {

    // Getters and Setters
    public List<QueryEvent> getBuffer();

    // Nick delegates
    public Nick getNick();
}
