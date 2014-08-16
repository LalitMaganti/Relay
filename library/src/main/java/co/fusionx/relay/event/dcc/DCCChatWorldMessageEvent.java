package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCChatConnection;

public class DCCChatWorldMessageEvent extends DCCChatEvent {

    public final String message;

    public DCCChatWorldMessageEvent(final DCCChatConnection dccConnection, final String message) {
        super(dccConnection);

        this.message = message;
    }
}