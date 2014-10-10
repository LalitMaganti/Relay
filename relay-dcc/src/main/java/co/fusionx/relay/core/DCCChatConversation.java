package co.fusionx.relay.core;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.event.chat.DCCChatEvent;
import co.fusionx.relay.sender.DCCChatSender;

public interface DCCChatConversation extends Conversation<DCCChatEvent>, DCCChatSender {
}