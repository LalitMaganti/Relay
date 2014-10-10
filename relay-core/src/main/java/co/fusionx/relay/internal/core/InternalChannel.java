package co.fusionx.relay.internal.core;

import java.util.Collection;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.event.channel.ChannelEvent;

public interface InternalChannel extends Channel, InternalConversation<ChannelEvent> {

    public Collection<InternalChannelUser> getUsers();

    public InternalChannel reset();

    public void addUser(final InternalChannelUser user);

    public void removeUser(final InternalChannelUser user);
}