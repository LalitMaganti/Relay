package co.fusionx.relay;

import java.util.List;

import co.fusionx.relay.event.query.QueryEvent;

public interface QueryUser extends Conversation {

    // Getters and Setters
    public List<QueryEvent> getBuffer();

    // Nick delegates
    public Nick getNick();
}
