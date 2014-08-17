package co.fusionx.relay.base;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.bus.ServerEventBus;
import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.sender.ServerSender;

public interface Server extends Conversation, ServerSender {

    public Collection<? extends ChannelUser> getUsers();

    public List<? extends ServerEvent> getBuffer();

    public UserChannelInterface getUserChannelInterface();

    public DCCManager getDCCManager();

    public ChannelUser getUser();

    public String getTitle();

    public ConnectionStatus getStatus();

    public ServerEventBus getServerEventBus();

    public ServerConfiguration getConfiguration();
}
