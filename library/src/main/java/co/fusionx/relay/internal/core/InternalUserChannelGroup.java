package co.fusionx.relay.internal.core;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Set;

import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.core.UserChannelGroup;
import co.fusionx.relay.internal.base.RelayChannel;

public interface InternalUserChannelGroup extends UserChannelGroup {

    public InternalLibraryUser getUser();

    public Optional<InternalChannel> getChannel(final String name);

    public Optional<InternalChannelUser> getUser(final String nick);

    public void coupleUserAndChannel(InternalChannelUser user, InternalChannel channel);

    public void coupleUserAndChannel(InternalChannelUser user, InternalChannel channel,
            UserLevel userLevel);

    public void decoupleUserAndChannel(InternalChannelUser user, InternalChannel channel);

    public Collection<InternalChannel> removeUser(InternalChannelUser user);

    public Collection<InternalChannelUser> removeChannel(InternalChannel channel);

    public void removeUserFromChannel(InternalChannel channel, InternalChannelUser user);

    public void removeChannelFromUser(InternalChannel channel, InternalChannelUser user);

    public InternalChannelUser getUserFromPrefix(String rawSource);

    public InternalChannelUser getNonNullUser(String nick);

    public InternalChannel getNewChannel(String channelName);

    public Set<InternalChannelUser> getUsers();

    public void onConnectionTerminated();
}
