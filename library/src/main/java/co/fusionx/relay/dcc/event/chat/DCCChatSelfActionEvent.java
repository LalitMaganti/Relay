package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatSelfActionEvent extends DCCChatEvent {

    public final String action;

    public DCCChatSelfActionEvent(final DCCChatConversation dccConnection, final String action) {
        super(dccConnection);

        this.action = action;
    }
}