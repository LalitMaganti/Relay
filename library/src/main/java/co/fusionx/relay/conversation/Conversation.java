package co.fusionx.relay.conversation;

import java.util.List;

import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.event.Event;

public interface Conversation<E extends Event> extends GenericBus {

    public String getId();

    public List<? extends E> getBuffer();

    /**
     * Returns whether the conversation is valid - i.e. is it attached and managed by the session
     *
     * @return the validity of the conversation
     */
    public boolean isValid();
}