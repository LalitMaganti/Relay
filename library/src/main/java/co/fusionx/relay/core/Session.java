package co.fusionx.relay.core;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.bus.GenericBus;

public interface Session {

    public SessionStatus getStatus();

    public GenericBus<Event> getSessionBus();

    public Server getServer();

    public UserChannelGroup getUserChannelManager();

    public QueryUserGroup getQueryManager();

    public DCCManager getDCCManager();
}