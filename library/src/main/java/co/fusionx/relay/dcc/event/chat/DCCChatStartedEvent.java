package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatStartedEvent extends DCCChatEvent {

    public DCCChatStartedEvent(final DCCChatConversation dccConnection) {
        super(dccConnection);
    }
}
