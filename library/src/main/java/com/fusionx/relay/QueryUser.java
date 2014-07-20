package com.fusionx.relay;

import com.fusionx.relay.event.query.QueryEvent;
import com.fusionx.relay.nick.Nick;

import java.util.List;

public interface QueryUser extends Conversation {

    // Getters and Setters
    public List<QueryEvent> getBuffer();

    // Nick delegates
    public Nick getNick();
}
