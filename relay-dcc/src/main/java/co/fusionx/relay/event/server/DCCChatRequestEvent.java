package co.fusionx.relay.event.server;

import co.fusionx.relay.internal.base.RelayDCCPendingChatConnection;
import co.fusionx.relay.conversation.Server;

public class DCCChatRequestEvent extends DCCRequestEvent {

    public DCCChatRequestEvent(final Server server,
            final RelayDCCPendingChatConnection pendingConnection) {
        super(server, pendingConnection);
    }

    @Override
    public RelayDCCPendingChatConnection getPendingConnection() {
        return (RelayDCCPendingChatConnection) pendingConnection;
    }
}