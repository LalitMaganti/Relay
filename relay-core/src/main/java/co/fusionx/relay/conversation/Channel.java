package co.fusionx.relay.conversation;

import java.util.Collection;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.sender.ChannelSender;

public interface Channel extends Conversation<ChannelEvent>, ChannelSender {

    public String getName();

    public Collection<? extends ChannelUser> getUsers();
}