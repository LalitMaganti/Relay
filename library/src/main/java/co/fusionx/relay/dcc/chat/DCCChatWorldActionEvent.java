package co.fusionx.relay.dcc.chat;

import co.fusionx.relay.dcc.event.chat.DCCChatEvent;

public class DCCChatWorldActionEvent extends DCCChatEvent {

    public final String action;

    public DCCChatWorldActionEvent(final DCCChatConversation conversation, final String action) {
        super(conversation);

        this.action = action;
    }
}
