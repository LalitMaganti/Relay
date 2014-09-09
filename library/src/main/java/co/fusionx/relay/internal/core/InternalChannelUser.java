package co.fusionx.relay.internal.core;

import java.util.Set;

import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;

public interface InternalChannelUser extends ChannelUser {

    @Override
    public Set<InternalChannel> getChannels();

    public void addChannel(InternalChannel channel, UserLevel level);

    public void removeChannel(InternalChannel channel);

    public void onModeChanged(InternalChannel channel, UserLevel mode);

    public void setNick(String nick);

    public boolean isNickEqual(String nick);

    public boolean isNickEqual(InternalChannelUser user);
}