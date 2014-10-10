package co.fusionx.relay.event;

import co.fusionx.relay.conversation.Conversation;

public class DCCEvent<T extends Conversation<U>, U extends DCCEvent> extends Event<T, U> {

    public DCCEvent(final T conversation) {
        super(conversation);
    }
}