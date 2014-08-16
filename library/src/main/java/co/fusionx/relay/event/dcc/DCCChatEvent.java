package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCConnection;

public class DCCChatEvent extends DCCEvent {

    public DCCChatEvent(final DCCConnection dccConnection) {
        super(dccConnection);
    }
}