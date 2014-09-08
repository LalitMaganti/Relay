package co.fusionx.relay.event;

import android.text.format.Time;

import co.fusionx.relay.base.Conversation;

public class Event<T extends Conversation<U>, U extends Event> {

    public final T conversation;

    public final Time timestamp;

    public Event(final T conversation) {
        this.conversation = conversation;

        this.timestamp = new Time();
        timestamp.setToNow();
    }
}