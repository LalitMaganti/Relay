package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.internal.dcc.base.RelayRelayDCCPendingChatConnection;

public class DCCChatRequestEvent extends DCCRequestEvent {

    public DCCChatRequestEvent(final Server server,
            final RelayRelayDCCPendingChatConnection pendingConnection) {
        super(server, pendingConnection);
    }

    @Override
    public RelayRelayDCCPendingChatConnection getPendingConnection() {
        return (RelayRelayDCCPendingChatConnection) pendingConnection;
    }
}