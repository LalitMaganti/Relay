package co.fusionx.relay.event;

import org.joda.time.DateTime;

import co.fusionx.relay.conversation.Conversation;

public class Event<T extends Conversation<U>, U extends Event> {

    public final T conversation;

    public final DateTime timestamp;

    public Event(final T conversation) {
        this.conversation = conversation;

        this.timestamp = DateTime.now();
    }
}