package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCConnection;

public class DCCChatEvent extends DCCEvent {

    private final String message;

    public DCCChatEvent(final DCCConnection dccConnection, final String message) {
        super(dccConnection);

        this.message = message;
    }
}