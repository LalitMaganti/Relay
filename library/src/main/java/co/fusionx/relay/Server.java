package co.fusionx.relay;

import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.bus.ServerEventBus;
import co.fusionx.relay.event.server.ServerEvent;

import java.util.Collection;
import java.util.List;

public interface Server extends Conversation {

    public void updateIgnoreList(Collection<String> list);

    public Collection<? extends ChannelUser> getUsers();

    // Getters and Setters
    public List<? extends ServerEvent> getBuffer();

    public UserChannelInterface getUserChannelInterface();

    public ChannelUser getUser();

    public String getTitle();

    public ConnectionStatus getStatus();

    public ServerCallHandler getServerCallHandler();

    public ServerEventBus getServerEventBus();

    public ServerConfiguration getConfiguration();
}
