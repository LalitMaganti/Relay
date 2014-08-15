package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public class DCCFileRequestEvent extends DCCRequestEvent {

    public final DCCPendingConnection pendingConnection;

    public DCCFileRequestEvent(final Server server, final DCCPendingConnection pendingConnection) {
        super(server);

        this.pendingConnection = pendingConnection;
    }
}
