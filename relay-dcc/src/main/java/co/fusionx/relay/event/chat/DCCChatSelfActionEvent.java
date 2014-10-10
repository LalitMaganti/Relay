package co.fusionx.relay.event.chat;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.core.DCCChatConversation;

public class DCCChatSelfActionEvent extends DCCChatEvent {

    public final LibraryUser user;

    public final String action;

    public DCCChatSelfActionEvent(final DCCChatConversation conversation,
            final LibraryUser user, final String action) {
        super(conversation);

        this.user = user;
        this.action = action;
    }
}