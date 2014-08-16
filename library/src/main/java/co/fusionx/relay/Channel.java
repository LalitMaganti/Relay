package co.fusionx.relay;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelEvent;

public interface Channel extends Conversation {

    public String getName();

    public List<? extends ChannelEvent> getBuffer();

    public Collection<? extends ChannelUser> getUsers();

    public int getUserCount();

    public int getNumberOfUsersType(final UserLevel userLevel);
}