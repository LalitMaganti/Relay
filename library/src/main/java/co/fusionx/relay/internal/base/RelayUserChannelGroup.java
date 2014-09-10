package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.SessionConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalLibraryUser;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayChannelSender;
import co.fusionx.relay.util.ParseUtils;

public class RelayUserChannelGroup implements InternalUserChannelGroup {

    private final Set<InternalChannelUser> mUsers;

    private final InternalLibraryUser mUser;

    private final GenericBus<Event> mSessionBus;

    private final SessionConfiguration mConfiguration;

    private final PacketSender mPacketSender;

    @Inject
    RelayUserChannelGroup(final GenericBus<Event> sessionBus,
            final SessionConfiguration configuration,
            final PacketSender packetSender) {
        mSessionBus = sessionBus;
        mConfiguration = configuration;
        mPacketSender = packetSender;

        // Set the nick name to the first choice nick
        mUser = new RelayLibraryUser(configuration.getConnectionConfiguration()
                .getNickStorage().getFirst());

        mUsers = new HashSet<>();
        mUsers.add(mUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalLibraryUser getUser() {
        return mUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InternalChannel> getChannel(final String name) {
        // Channel names have to unique disregarding case - not having ignore-case here leads
        // to null channels when the channel does actually exist
        return FluentIterable.from(mUser.getChannels())
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InternalChannelUser> getUser(final String nick) {
        return FluentIterable.from(mUsers)
                .filter(u -> nick.equals(u.getNick().getNickAsString()))
                .first();
    }

    /**
     * Add the channel to the user and user to the channel. Also add the user to the global list
     * of users. The user is given a default user level in the channel of {@link
     * co.fusionx.relay.constants.UserLevel#NONE}
     *
     * @param user    the user to add to the channel
     * @param channel the channel to add to the user
     */
    @Override
    public void coupleUserAndChannel(final InternalChannelUser user,
            final InternalChannel channel) {
        coupleUserAndChannel(user, channel, UserLevel.NONE);
    }

    /**
     * Add the channel to the user and user to the channel. Also add the user to the global list
     * of users. The user is given the user level in the channel as specified by userLevel
     *
     * @param user      the user to add to the channel
     * @param channel   the channel to add to the user
     * @param userLevel the level to give the user in the channel
     */
    @Override
    public void coupleUserAndChannel(final InternalChannelUser user, final InternalChannel channel,
            final UserLevel userLevel) {
        addUserToChannel(channel, user);
        addChannelToUser(channel, user, userLevel);
    }

    /**
     * Remove the channel from the user and the user from the channel. Also if this channel is
     * the last one that we know the user has joined then remove the user from the global list
     *
     * @param user    the user to remove from the channel and/or remove it from the global list
     * @param channel the channel to remove from the user
     */
    @Override
    public void decoupleUserAndChannel(final InternalChannelUser user,
            final InternalChannel channel) {
        removeUserFromChannel(channel, user);
        removeChannelFromUser(channel, user);
    }

    /**
     * Remove the user from the global list and return the channels the user joined
     *
     * @param user the user to remove from the global list
     * @return the channels the user had joined
     */
    @Override
    public Collection<InternalChannel> removeUser(final InternalChannelUser user) {
        mUsers.remove(user);
        return user.getChannels();
    }

    /**
     * Remove the channel from our list of channels and return the users in the channel
     *
     * @param channel the channel to remove
     * @return the users that were in the channel
     */
    @Override
    public Collection<InternalChannelUser> removeChannel(final InternalChannel channel) {
        mUser.getChannels().remove(channel);
        channel.markInvalid();
        return channel.getUsers();
    }

    /**
     * Add the user to the list of users of the channel
     *
     * @param channel the channel to add the user to
     * @param user    the user to add to the channel
     */
    void addUserToChannel(final InternalChannel channel, final InternalChannelUser user) {
        channel.addUser(user);
    }

    /**
     * Add the channel to the list of channels of the user
     *
     * @param channel   the channel to add to the user
     * @param user      the user to add to the channel to
     * @param userLevel the level to give the user in the channel
     */
    void addChannelToUser(final InternalChannel channel, final InternalChannelUser user,
            final UserLevel userLevel) {
        user.addChannel(channel, userLevel);

        // Also remember to add the user to the global list
        mUsers.add(user);
    }

    /**
     * Removes the channel from the list of channels in the user
     *
     * @param channel the channel to remove from the user
     * @param user    the user the channel is to be removed from
     */
    @Override
    public void removeUserFromChannel(InternalChannel channel, InternalChannelUser user) {
        channel.removeUser(user);
    }

    /**
     * Removes the channel from the user and if this was the last channel we knew the user was
     * in, remove the channel from the global list of users
     *
     * @param channel the channel to remove from the user
     * @param user    the user to remove the channel from or remove from the global list
     */
    @Override
    public void removeChannelFromUser(final InternalChannel channel,
            final InternalChannelUser user) {
        final Collection<? extends Channel> setOfChannels = user.getChannels();
        user.removeChannel(channel);

        // The app user check is to make sure that the app user isn't removed from the list of
        // users
        if (setOfChannels.size() == 0 && !(user instanceof InternalLibraryUser)) {
            mUsers.remove(user);
        }
    }

    /**
     * Get the user by source from the list of users which are in all the channels we know about
     *
     * @param rawSource the source of the user to retrieve
     * @return the user matching the source or null of none match
     */
    @Override
    public InternalChannelUser getUserFromPrefix(final String rawSource) {
        final String nick = ParseUtils.getNickFromPrefix(rawSource);
        return getNonNullUser(nick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalChannelUser getNonNullUser(final String nick) {
        return getUser(nick).or(new RelayChannelUser(nick));
    }

    @Override
    public InternalChannel getNewChannel(final String channelName) {
        final RelayChannelSender channelSender = new RelayChannelSender(mConfiguration
                .getSettingsProvider(), mPacketSender, mUser);
        final InternalChannel channel = new RelayChannel(mSessionBus,
                mConfiguration.getConnectionConfiguration(), channelSender, channelName);

        // Horrible but has to be done - see the comment on the method
        channelSender.setChannel(channel);
        return channel;
    }

    @Override
    public Set<InternalChannelUser> getUsers() {
        return mUsers;
    }

    @Override
    public void onConnectionTerminated() {
        // Clear the global list of users - it's now invalid
        mUsers.clear();

        // Keep our own user inside though
        mUsers.add(mUser);
    }
}