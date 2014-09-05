package co.fusionx.relay.base;

import java.util.Collection;

import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.sender.ServerSender;

public interface Server extends Conversation<ServerEvent>, ServerSender {

    public Collection<? extends ChannelUser> getUsers();

    public UserChannelInterface getUserChannelInterface();

    public DCCManager getDCCManager();

    public ChannelUser getUser();

    public String getTitle();

    public ConnectionStatus getStatus();

    public EventBus<Event> getServerWideBus();

    public ServerConfiguration getConfiguration();
}
