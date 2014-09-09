package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;

public class DCCChatRequestEvent extends DCCRequestEvent {

    public DCCChatRequestEvent(final Server server,
            final DCCPendingChatConnection pendingConnection) {
        super(server, pendingConnection);
    }

    @Override
    public DCCPendingChatConnection getPendingConnection() {
        return (DCCPendingChatConnection) pendingConnection;
    }
}