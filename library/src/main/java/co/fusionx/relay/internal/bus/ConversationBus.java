package co.fusionx.relay.internal.bus;

import com.google.common.base.Function;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.event.Event;

public class ConversationBus<T extends Conversation<U>,
        U extends Event<T, U>> extends BufferingBus<U> {

    private final T mConversation;

    public ConversationBus(final T conversation, final GenericBus<U> bus) {
        super(bus);

        mConversation = conversation;
    }

    public void post(final Function<T, ? extends U> function) {
        post(function.apply(mConversation));
    }
}