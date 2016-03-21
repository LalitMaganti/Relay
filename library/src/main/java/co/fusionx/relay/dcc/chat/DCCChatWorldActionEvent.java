package co.fusionx.relay.dcc.chat;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.dcc.event.chat.DCCChatEvent;

public class DCCChatWorldActionEvent extends DCCChatEvent {

    public final String action;
    public final List<FormatSpanInfo> formats;

    public DCCChatWorldActionEvent(final DCCChatConversation conversation,
            final String action, final List<FormatSpanInfo> formats) {
        super(conversation);

        this.action = action;
        this.formats = formats;
    }
}
