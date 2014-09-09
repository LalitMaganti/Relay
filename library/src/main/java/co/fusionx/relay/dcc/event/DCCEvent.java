package co.fusionx.relay.dcc.event;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.event.Event;

public class DCCEvent<T extends Conversation<U>, U extends DCCEvent> extends Event<T, U> {

    public DCCEvent(final T conversation) {
        super(conversation);
    }
}