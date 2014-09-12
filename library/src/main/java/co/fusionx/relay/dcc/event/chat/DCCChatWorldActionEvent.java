package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.internal.dcc.base.RelayDCCChatConversation;

public class DCCChatWorldActionEvent extends DCCChatEvent {

    public final String action;

    public DCCChatWorldActionEvent(final RelayDCCChatConversation conversation, final String action) {
        super(conversation);

        this.action = action;
    }
}
