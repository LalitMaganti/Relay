package co.fusionx.relay.core;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.dcc.DCCManager;

public interface Session extends Registerable {

    public SessionStatus getStatus();

    public Server getServer();

    public UserChannelGroup getUserChannelManager();

    public QueryUserGroup getQueryManager();

    public DCCManager getDCCManager();
}