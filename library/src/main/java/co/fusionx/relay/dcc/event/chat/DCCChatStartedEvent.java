package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.internal.dcc.base.RelayDCCChatConversation;

public class DCCChatStartedEvent extends DCCChatEvent {

    public DCCChatStartedEvent(final RelayDCCChatConversation dccConnection) {
        super(dccConnection);
    }
}
