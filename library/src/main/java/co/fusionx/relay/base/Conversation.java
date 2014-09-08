package co.fusionx.relay.base;

import java.util.List;

import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.EventBus;

public interface Conversation<E extends Event> {

    public String getId();

    public List<? extends E> getBuffer();

    public EventBus<Event> getConnectionWideBus();

    public EventBus<? extends E> getBus();

    /**
     * Returns whether the conversation is valid - i.e. is it attached and managed by the server
     *
     * @return the validity of the conversation
     */
    public boolean isValid();
}