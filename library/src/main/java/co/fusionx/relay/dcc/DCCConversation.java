package co.fusionx.relay.dcc;

import co.fusionx.relay.base.Conversation;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.relay.RelayServer;

public abstract class DCCConversation implements Conversation {

    protected final RelayServer mServer;

    public DCCConversation(final RelayServer server) {
        mServer = server;
    }

    @Override
    public Server getServer() {
        return mServer;
    }

    // TODO - this has not been implemented
    @Override
    public boolean isValid() {
        return true;
    }
}