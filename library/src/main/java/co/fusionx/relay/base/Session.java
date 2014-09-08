package co.fusionx.relay.base;

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