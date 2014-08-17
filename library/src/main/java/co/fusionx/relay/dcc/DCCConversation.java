package co.fusionx.relay.dcc;

import co.fusionx.relay.Conversation;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.Server;

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