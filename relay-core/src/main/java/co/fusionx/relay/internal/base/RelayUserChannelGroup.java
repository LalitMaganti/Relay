package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalLibraryUser;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.core.Postable;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayChannelSender;
import co.fusionx.relay.util.ParseUtils;

public class RelayUserChannelGroup implements InternalUserChannelGroup {

    private final Set<InternalChannelUser> mUsers;

    private final InternalLibraryUser mUser;

    private final Postable<Event> mSessionBus;

    private final SessionConfiguration mConfiguration;

    private final PacketSender mPacketSender;

    @Inject
    RelayUserChannelGroup(final Postable<Event> sessionBus,
            final SessionConfiguration configuration,
            final PacketSender packetSender) {
        mSessionBus = sessionBus;
        mConfiguration = configuration;
        mPacketSender = packetSender;

        // Set the nick name to the first choice nick
        mUser = new RelayLibraryUser(configuration.getConnectionConfiguration().
                getNickProvider().getFirst());

        mUsers = new HashSet<>();
        mUsers.add(mUser);
    }

    @Override
    public InternalLibraryUser getUser() {
        return mUser;
    }

    @Override
    public Optional<InternalChannel> getChannel(final String name) {
        // Channel names have to unique disregarding case - not having ignore-case here leads
        // to null channels when the channel does actually exist
        return FluentIterable.from(mUser.getChannels())
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .first();
    }

    @Override
    public Optional<InternalChannelUser> getUser(final String nick) {
        return FluentIterable.from(mUsers)
                .filter(u -> nick.equals(u.getNick().getNickAsString()))
                .first();
    }

    @Override
    public void coupleUserAndChannel(final InternalChannelUser user,
            final InternalChannel channel) {
        coupleUserAndChannel(user, channel, UserLevel.NONE);
    }

    @Override
    public void coupleUserAndChannel(final InternalChannelUser user, final InternalChannel channel,
            final UserLevel userLevel) {
        addUserToChannel(channel, user);
        addChannelToUser(channel, user, userLevel);
    }

    @Override
    public void decoupleUserAndChannel(final InternalChannelUser user,
            final InternalChannel channel) {
        removeUserFromChannel(channel, user);
        removeChannelFromUser(channel, user);
    }

    @Override
    public Collection<InternalChannel> removeUser(final InternalChannelUser user) {
        mUsers.remove(user);
        return user.getChannels();
    }

    @Override
    public Collection<InternalChannelUser> removeChannel(final InternalChannel channel) {
        mUser.getChannels().remove(channel);
        channel.markInvalid();
        return channel.getUsers();
    }

    @Override
    public void removeUserFromChannel(InternalChannel channel, InternalChannelUser user) {
        channel.removeUser(user);
    }

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

    @Override
    public InternalChannelUser getUserFromPrefix(final String rawSource) {
        final String nick = ParseUtils.getNickFromPrefix(rawSource);
        return getNonNullUser(nick);
    }

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
}