package co.fusionx.relay.event.server;

import co.fusionx.relay.internal.base.RelayRelayDCCPendingChatConnection;
import co.fusionx.relay.conversation.Server;

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