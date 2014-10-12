package co.fusionx.relay.event.server;

import co.fusionx.relay.internal.base.RelayDCCPendingConnection;
import co.fusionx.relay.conversation.Server;

public abstract class DCCRequestEvent extends ServerEvent {

    public final RelayDCCPendingConnection pendingConnection;

    public DCCRequestEvent(final Server server, final RelayDCCPendingConnection pendingConnection) {
        super(server);

        this.pendingConnection = pendingConnection;
    }

    public abstract RelayDCCPendingConnection getPendingConnection();
}
