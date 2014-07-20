package com.fusionx.relay;

import com.fusionx.relay.bus.ServerCallBus;
import com.fusionx.relay.bus.ServerEventBus;
import com.fusionx.relay.event.server.ServerEvent;

import java.util.Collection;
import java.util.List;

public interface Server extends Conversation {

    void updateIgnoreList(Collection<String> list);

    Collection<? extends ChannelUser> getUsers();

    // Getters and Setters
    List<? extends ServerEvent> getBuffer();

    UserChannelInterface getUserChannelInterface();

    AppUser getUser();

    String getTitle();

    ConnectionStatus getStatus();

    ServerCallBus getServerCallBus();

    ServerEventBus getServerEventBus();

    ServerConfiguration getConfiguration();
}
