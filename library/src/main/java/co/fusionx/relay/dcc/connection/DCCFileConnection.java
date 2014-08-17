package co.fusionx.relay.dcc.connection;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public abstract class DCCFileConnection extends DCCConnection {

    public DCCFileConnection(final RelayServer server,
            final DCCPendingConnection pendingConnection) {
        super(server, pendingConnection);
    }

}