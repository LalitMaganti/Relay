package co.fusionx.relay.core;

import java.util.Set;

import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.conversation.Channel;

public interface ChannelUser {

    public Nick getNick();

    public Set<? extends Channel> getChannels();

    public UserLevel getChannelPrivileges(final Channel channel);
}