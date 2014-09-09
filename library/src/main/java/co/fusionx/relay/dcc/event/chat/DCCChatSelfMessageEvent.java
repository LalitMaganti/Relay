package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatSelfMessageEvent extends DCCChatEvent {

    public final String message;

    public final ChannelUser mainUser;

    public DCCChatSelfMessageEvent(final DCCChatConversation dccConnection,
            final ChannelUser mainUser, final String message) {
        super(dccConnection);

        this.mainUser = mainUser;
        this.message = message;
    }
}