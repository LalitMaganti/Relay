package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.dcc.core.DCCChatConversation;
import co.fusionx.relay.dcc.event.DCCEvent;

public class DCCChatEvent extends DCCEvent<DCCChatConversation, DCCChatEvent> {

    public DCCChatEvent(final DCCChatConversation conversation) {
        super(conversation);
    }
}