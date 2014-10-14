package co.fusionx.relay.internal.core;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Set;

import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.core.UserChannelGroup;

public interface InternalUserChannelGroup extends UserChannelGroup {

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalLibraryUser getUser();

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InternalChannel> getChannel(final String name);

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InternalChannelUser> getUser(final String nick);

    /**
     * Add the channel to the user and user to the channel. Also add the user to the global list
     * of users. The user is given a default user level in the channel of {@link
     * co.fusionx.relay.constant.UserLevel#NONE}
     *
     * @param user    the user to add to the channel
     * @param channel the channel to add to the user
     */
    public void coupleUserAndChannel(InternalChannelUser user, InternalChannel channel);

    /**
     * Add the channel to the user and user to the channel. Also add the user to the global list
     * of users. The user is given the user level in the channel as specified by userLevel
     *
     * @param user      the user to add to the channel
     * @param channel   the channel to add to the user
     * @param userLevel the level to give the user in the channel
     */
    public void coupleUserAndChannel(InternalChannelUser user, InternalChannel channel,
            UserLevel userLevel);

    /**
     * Remove the channel from the user and the user from the channel. Also if this channel is
     * the last one that we know the user has joined then remove the user from the global list
     *
     * @param user    the user to remove from the channel and/or remove it from the global list
     * @param channel the channel to remove from the user
     */
    public void decoupleUserAndChannel(InternalChannelUser user, InternalChannel channel);

    /**
     * Remove the user from the global list and return the channels the user joined
     *
     * @param user the user to remove from the global list
     * @return the channels the user had joined
     */
    public Collection<InternalChannel> removeUser(InternalChannelUser user);

    /**
     * Remove the channel from our list of channels and return the users in the channel
     *
     * @param channel the channel to remove
     * @return the users that were in the channel
     */
    public Collection<InternalChannelUser> removeChannel(InternalChannel channel);

    /**
     * Removes the channel from the list of channels in the user
     *
     * @param channel the channel to remove from the user
     * @param user    the user the channel is to be removed from
     */
    public void removeUserFromChannel(InternalChannel channel, InternalChannelUser user);

    /**
     * Removes the channel from the user and if this was the last channel we knew the user was
     * in, remove the channel from the global list of users
     *
     * @param channel the channel to remove from the user
     * @param user    the user to remove the channel from or remove from the global list
     */
    public void removeChannelFromUser(InternalChannel channel, InternalChannelUser user);

    /**
     * Get the user by source from the list of users which are in all the channels we know about
     *
     * @param rawSource the source of the user to retrieve
     * @return the user matching the source or null of none match
     */
    public InternalChannelUser getUserFromPrefix(String rawSource);

    public InternalChannelUser getNonNullUser(String nick);

    public InternalChannel getNewChannel(String channelName);

    public Set<InternalChannelUser> getUsers();

    public void onConnectionTerminated();
}
