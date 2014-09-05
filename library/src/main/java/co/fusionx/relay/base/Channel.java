package co.fusionx.relay.base;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.sender.ChannelSender;

public interface Channel extends Conversation, ChannelSender {

    public String getName();

    public List<? extends ChannelEvent> getBuffer();

    public Collection<? extends ChannelUser> getUsers();
}