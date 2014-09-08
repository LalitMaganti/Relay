package co.fusionx.relay.base;

import java.util.Set;

import co.fusionx.relay.constants.UserLevel;

public interface ChannelUser {

    public Nick getNick();

    public Set<? extends Channel> getChannels();

    public UserLevel getChannelPrivileges(final Channel channel);
}