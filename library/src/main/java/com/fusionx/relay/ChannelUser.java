package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;

import java.util.Set;

public interface ChannelUser {

    public Set<? extends Channel> getChannels();

    public UserLevel getChannelPrivileges(final Channel channel);

    public Nick getNick();
}