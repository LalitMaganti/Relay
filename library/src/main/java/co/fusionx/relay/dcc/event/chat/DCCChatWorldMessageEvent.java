package co.fusionx.relay.dcc.event.chat;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.dcc.chat.DCCChatConversation;

public class DCCChatWorldMessageEvent extends DCCChatEvent {

    public final String message;
    public final List<FormatSpanInfo> formats;

    public DCCChatWorldMessageEvent(final DCCChatConversation dccConnection,
            final String message, final List<FormatSpanInfo> formats) {
        super(dccConnection);

        this.message = message;
        this.formats = formats;
    }
}