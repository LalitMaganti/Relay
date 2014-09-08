package co.fusionx.relay.dcc.event.chat;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatSelfActionEvent extends DCCChatEvent {

    public final ChannelUser mainUser;

    public final String action;

    public DCCChatSelfActionEvent(final DCCChatConversation dccConnection,
            final ChannelUser mainUser, final String action) {
        super(dccConnection);

        this.mainUser = mainUser;
        this.action = action;
    }
}