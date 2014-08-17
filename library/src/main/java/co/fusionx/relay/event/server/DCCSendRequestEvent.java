package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;
import co.fusionx.relay.dcc.pending.DCCPendingFileConnection;

public class DCCSendRequestEvent extends DCCRequestEvent {

    public DCCSendRequestEvent(final Server server,
            final DCCPendingFileConnection pendingConnection) {
        super(server, pendingConnection);
    }

    @Override
    public DCCPendingFileConnection getPendingConnection() {
        return (DCCPendingFileConnection) pendingConnection;
    }
}
