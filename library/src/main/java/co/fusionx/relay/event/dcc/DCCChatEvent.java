package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCChatConnection;

public class DCCChatEvent extends DCCEvent {

    public DCCChatEvent(final DCCChatConnection dccConnection) {
        super(dccConnection);
    }

    public DCCChatConnection getConnection() {
        return (DCCChatConnection) dccConnection;
    }
}