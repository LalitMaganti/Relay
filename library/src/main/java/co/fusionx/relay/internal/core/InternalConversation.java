package co.fusionx.relay.internal.core;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.bus.PostableBus;

public interface InternalConversation<T extends Event> extends Conversation<T>, PostableBus<T> {

    public void markInvalid();
}