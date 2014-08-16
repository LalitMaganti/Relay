package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCChatConnection;

public class DCCChatStartedEvent extends DCCEvent {

    public DCCChatStartedEvent(final DCCChatConnection dccConnection) {
        super(dccConnection);
    }
}
