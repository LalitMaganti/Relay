package co.fusionx.relay.internal.core;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.event.Event;

public interface InternalConversation<T extends Event> extends Conversation<T>, Postable<T> {

    public void markInvalid();
}