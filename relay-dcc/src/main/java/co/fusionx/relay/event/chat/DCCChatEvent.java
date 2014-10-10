package co.fusionx.relay.event.chat;

import co.fusionx.relay.core.DCCChatConversation;
import co.fusionx.relay.event.DCCEvent;

public class DCCChatEvent extends DCCEvent<DCCChatConversation, DCCChatEvent> {

    public DCCChatEvent(final DCCChatConversation conversation) {
        super(conversation);
    }
}