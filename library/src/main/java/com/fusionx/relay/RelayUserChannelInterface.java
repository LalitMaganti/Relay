package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import gnu.trove.set.hash.THashSet;

public class RelayUserChannelInterface implements UserChannelInterface {

    private final Collection<RelayQueryUser> mQueryUsers;

    private final RelayServer mServer;

    private Set<String> mUserIgnoreList;

    RelayUserChannelInterface(final RelayServer server) {
        mServer = server;

        mQueryUsers = new LinkedHashSet<>();
        mUserIgnoreList = new HashSet<>();
    }

    /**
     * Add the channel to the user and user to the channel. Also add the user to the global list
     * of users. The user is given a default user level in the channel of {@link UserLevel#NONE}
     *
     * @param user    the user to add to the channel
     * @param channel the channel to add to the user
     */
    public void coupleUserAndChannel(final RelayChannelUser user, final RelayChannel channel) {
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
    public void coupleUserAndChannel(final RelayChannelUser user, final RelayChannel channel,
            final UserLevel userLevel) {
        addUserToChannel(channel, user, userLevel);
        addChannelToUser(channel, user, userLevel);
    }

    /**
     * Remove the channel from the user and the user from the channel. Also if this channel is
     * the last one that we know the user has joined then remove the user from the global list
     *
     * @param user    the user to remove from the channel and/or remove it from the global list
     * @param channel the channel to remove from the user
     */
    public void decoupleUserAndChannel(final RelayChannelUser user, final RelayChannel channel) {
        removeUserFromChannel(channel, user);
        removeChannelFromUser(channel, user);
    }

    /**
     * Remove the user from the global list and return the channels the user joined
     *
     * @param user the user to remove from the global list
     * @return the channels the user had joined
     */
    public Collection<RelayChannel> removeUser(final RelayChannelUser user) {
        mServer.removeUser(user);
        return user.getChannels();
    }

    /**
     * Remove the channel from our list of channels and return the users in the channel
     *
     * @param channel the channel to remove
     * @return the users that were in the channel
     */
    public Collection<RelayChannelUser> removeChannel(final RelayChannel channel) {
        mServer.getUser().getChannels().remove(channel);
        channel.markInvalid();
        return channel.getUsers();
    }

    /**
     * Add the user to the list of users of the channel
     *
     * @param channel   the channel to add the user to
     * @param user      the user to add to the channel
     * @param userLevel the level to give the user in the channel
     */
    public void addUserToChannel(final RelayChannel channel, final RelayChannelUser user,
            final UserLevel userLevel) {
        channel.addUser(user, userLevel);
    }

    /**
     * Add the channel to the list of channels of the user
     *
     * @param channel   the channel to add to the user
     * @param user      the user to add to the channel to
     * @param userLevel the level to give the user in the channel
     */
    public void addChannelToUser(final RelayChannel channel, final RelayChannelUser user,
            final UserLevel userLevel) {
        user.addChannel(channel, userLevel);

        // Also remember to add the user to the global list
        mServer.addUser(user);
    }

    /**
     * Removes the channel from the list of channels in the user
     *
     * @param channel the channel to remove from the user
     * @param user    the user the channel is to be removed from
     */
    public void removeUserFromChannel(RelayChannel channel, RelayChannelUser user) {
        channel.removeUser(user);
    }

    /**
     * Removes the channel from the user and if this was the last channel we knew the user was
     * in, remove the channel from the global list of users
     *
     * @param channel the channel to remove from the user
     * @param user    the user to remove the channel from or remove from the global list
     */
    public void removeChannelFromUser(final RelayChannel channel, final RelayChannelUser user) {
        final Collection<RelayChannel> setOfChannels = user.getChannels();
        // The app user check is to make sure that the app user isn't removed from the list of
        // users
        if (setOfChannels.size() > 1 || user instanceof AppUser) {
            user.removeChannel(channel);
        } else {
            mServer.removeUser(user);
        }
    }

    /**
     * Get the channel by name from the list of channels which have been joined by the user
     *
     * @param name the name of channel to retrieve
     * @return the channel matching the specified name or null if none match
     */
    @Override
    public RelayChannel getChannel(final String name) {
        for (final RelayChannel channel : mServer.getUser().getChannels()) {
            // Channel names have to unique disregarding case - not having ignore-case here leads
            // to null channels when the channel does actually exist
            if (name.equalsIgnoreCase(channel.getName())) {
                return channel;
            }
        }
        return null;
    }

    /**
     * Get the user by source from the list of users which are in all the channels we know about
     *
     * @param rawSource the source of the user to retrieve
     * @return the user matching the source or null of none match
     */
    public RelayChannelUser getUserFromRaw(final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        return getNonNullUser(nick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelayChannelUser getUser(final String nick) {
        for (final RelayChannelUser user : mServer.getUsers()) {
            if (nick.equals(user.getNick().getNickAsString())) {
                return user;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public RelayChannelUser getNonNullUser(final String nick) {
        final RelayChannelUser user = getUser(nick);
        return user != null ? user : new RelayChannelUser(nick);
    }

    public RelayChannel getNewChannel(final String channelName) {
        return new RelayChannel(mServer, channelName);
    }

    @Override
    public Collection<RelayQueryUser> getQueryUsers() {
        return mQueryUsers;
    }

    @Override
    public RelayQueryUser getQueryUser(final String nick) {
        for (final RelayQueryUser user : mQueryUsers) {
            if (nick.equals(user.getNick().getNickAsString())) {
                return user;
            }
        }
        return null;
    }

    public void addQueryUser(final String nick, final String message, final boolean action,
            final boolean userSent) {
        final RelayQueryUser user = new RelayQueryUser(nick, mServer, message, action, userSent);
        mQueryUsers.add(user);
    }

    public void removeQueryUser(final RelayQueryUser user) {
        mQueryUsers.remove(user);
        user.markInvalid();
    }

    public boolean shouldIgnoreUser(final String userNick) {
        return mUserIgnoreList.contains(userNick);
    }

    // Getters and setters
    void updateIgnoreList(final Collection<String> userIgnoreList) {
        if (userIgnoreList != null) {
            mUserIgnoreList = new THashSet<>(userIgnoreList);
        }
    }
}