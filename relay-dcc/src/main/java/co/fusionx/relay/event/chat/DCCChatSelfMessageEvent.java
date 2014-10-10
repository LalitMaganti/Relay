package co.fusionx.relay.event.chat;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.core.DCCChatConversation;

public class DCCChatSelfMessageEvent extends DCCChatEvent {

    public final LibraryUser user;

    public final String message;

    public DCCChatSelfMessageEvent(final DCCChatConversation conversation, final LibraryUser user,
            final String message) {
        super(conversation);

        this.user = user;
        this.message = message;
    }
}