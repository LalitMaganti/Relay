package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCChatConnection;

public class DCCChatSelfMessageEvent extends DCCChatEvent {

    public final String message;

    public DCCChatSelfMessageEvent(final DCCChatConnection dccConnection, final String message) {
        super(dccConnection);

        this.message = message;
    }
}