package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatWorldMessageEvent extends DCCChatEvent {

    public final String message;

    public DCCChatWorldMessageEvent(final DCCChatConversation dccConnection, final String message) {
        super(dccConnection);

        this.message = message;
    }
}