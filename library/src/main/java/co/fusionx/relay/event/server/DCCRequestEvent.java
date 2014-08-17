package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public abstract class DCCRequestEvent extends ServerEvent {

    public final DCCPendingConnection pendingConnection;

    public DCCRequestEvent(final Server server, final DCCPendingConnection pendingConnection) {
        super(server);
        this.pendingConnection = pendingConnection;
    }

    public abstract DCCPendingConnection getPendingConnection();
}
