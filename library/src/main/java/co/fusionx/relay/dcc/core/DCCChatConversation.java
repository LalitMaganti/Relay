package co.fusionx.relay.dcc.core;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.dcc.event.chat.DCCChatEvent;
import co.fusionx.relay.dcc.sender.DCCChatSender;

public interface DCCChatConversation extends Conversation<DCCChatEvent>, DCCChatSender {
}