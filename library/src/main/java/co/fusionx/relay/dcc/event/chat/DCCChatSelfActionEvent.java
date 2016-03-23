package co.fusionx.relay.dcc.event.chat;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatSelfActionEvent extends DCCChatEvent {

    public final String action;
    public final List<FormatSpanInfo> formats;

    public DCCChatSelfActionEvent(final DCCChatConversation dccConnection,
            final String action, final List<FormatSpanInfo> formats) {
        super(dccConnection);

        this.action = action;
        this.formats = formats;
    }
}