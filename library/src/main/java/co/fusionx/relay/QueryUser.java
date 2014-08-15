package co.fusionx.relay;

import co.fusionx.relay.event.query.QueryEvent;

import java.util.List;

public interface QueryUser extends Conversation {

    // Getters and Setters
    public List<QueryEvent> getBuffer();

    // Nick delegates
    public Nick getNick();
}
