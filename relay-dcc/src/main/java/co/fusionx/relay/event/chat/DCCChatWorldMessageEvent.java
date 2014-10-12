package co.fusionx.relay.event.chat;

import co.fusionx.relay.internal.base.RelayDCCChatConversation;

public class DCCChatWorldMessageEvent extends DCCChatEvent {

    public final String message;

    public DCCChatWorldMessageEvent(final RelayDCCChatConversation dccConnection, final String message) {
        super(dccConnection);

        this.message = message;
    }
}