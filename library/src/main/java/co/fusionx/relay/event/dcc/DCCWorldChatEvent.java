package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCChatConnection;

public class DCCWorldChatEvent extends DCCEvent {

    private final String message;

    public DCCWorldChatEvent(final DCCChatConnection dccConnection, final String message) {
        super(dccConnection);

        this.message = message;
    }
}