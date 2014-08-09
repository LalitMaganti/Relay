package com.fusionx.relay;

public interface Conversation {

    public String getId();

    public Server getServer();

    public boolean isConversationValid();
}