package co.fusionx.relay.event.chat;

import co.fusionx.relay.base.RelayDCCChatConversation;

public class DCCChatStartedEvent extends DCCChatEvent {

    public DCCChatStartedEvent(final RelayDCCChatConversation dccConnection) {
        super(dccConnection);
    }
}
