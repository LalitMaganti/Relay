package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.internal.dcc.base.RelayDCCPendingConnection;

public abstract class DCCRequestEvent extends ServerEvent {

    public final RelayDCCPendingConnection pendingConnection;

    public DCCRequestEvent(final Server server, final RelayDCCPendingConnection pendingConnection) {
        super(server);
        this.pendingConnection = pendingConnection;
    }

    public abstract RelayDCCPendingConnection getPendingConnection();
}
