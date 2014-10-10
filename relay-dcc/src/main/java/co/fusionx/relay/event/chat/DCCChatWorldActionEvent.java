package co.fusionx.relay.event.chat;

import co.fusionx.relay.base.RelayDCCChatConversation;

public class DCCChatWorldActionEvent extends DCCChatEvent {

    public final String action;

    public DCCChatWorldActionEvent(final RelayDCCChatConversation conversation, final String action) {
        super(conversation);

        this.action = action;
    }
}
