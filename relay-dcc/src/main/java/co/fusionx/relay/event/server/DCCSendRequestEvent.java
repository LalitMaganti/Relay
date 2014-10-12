package co.fusionx.relay.event.server;

import co.fusionx.relay.internal.base.RelayDCCPendingSendConnection;
import co.fusionx.relay.conversation.Server;

public class DCCSendRequestEvent extends DCCRequestEvent {

    public DCCSendRequestEvent(final Server server,
            final RelayDCCPendingSendConnection pendingConnection) {
        super(server, pendingConnection);
    }

    @Override
    public RelayDCCPendingSendConnection getPendingConnection() {
        return (RelayDCCPendingSendConnection) pendingConnection;
    }
}
