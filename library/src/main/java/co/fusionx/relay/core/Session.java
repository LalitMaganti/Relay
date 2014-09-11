package co.fusionx.relay.core;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.bus.GenericBus;

public interface Session extends GenericBus {

    public SessionStatus getStatus();

    public Server getServer();

    public UserChannelGroup getUserChannelManager();

    public QueryUserGroup getQueryManager();

    public DCCManager getDCCManager();
}