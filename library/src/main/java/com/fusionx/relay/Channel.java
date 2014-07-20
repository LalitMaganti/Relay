package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.event.channel.ChannelEvent;

import java.util.Collection;
import java.util.List;

public interface Channel extends Conversation {

    public String getName();

    public List<? extends ChannelEvent> getBuffer();

    public Collection<? extends ChannelUser> getUsers();

    public int getUserCount();

    public int getNumberOfUsersType(final UserLevel userLevel);
}