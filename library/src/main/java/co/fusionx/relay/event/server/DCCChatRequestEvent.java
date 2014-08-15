package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public class DCCChatRequestEvent extends ServerEvent {

    public final DCCPendingConnection pendingConnection;

    public DCCChatRequestEvent(final Server server, final DCCPendingConnection pendingConnection) {
        super(server);

        this.pendingConnection = pendingConnection;
    }
}