package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.Set;

import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

public final class UserChannelInterface {

    private final Collection<QueryUser> mQueryUsers;

    private final Server mServer;

    private Set<String> mUserIgnoreList;

    public UserChannelInterface(final Server server) {
        mServer = server;

        mQueryUsers = new TLinkedHashSet<>();
        mUserIgnoreList = new THashSet<>();
    }

    /**
     *
     * @param user
     * @param channel
     */
    public void coupleUserAndChannel(final ChannelUser user, final Channel channel) {
        coupleUserAndChannel(user, channel, UserLevel.NONE);
    }

    /**
     *
     * @param user
     * @param channel
     * @param userLevel
     */
    public void coupleUserAndChannel(final ChannelUser user, final Channel channel,
            final UserLevel userLevel) {
        addUserToChannel(channel, user, userLevel);
        addChannelToUser(channel, user, userLevel);
    }

    /**
     *
     * @param user
     * @param channel
     */
    public void decoupleUserAndChannel(final ChannelUser user, final Channel channel) {
        removeUserFromChannel(channel, user);
        removeChannelFromUser(channel, user);
    }

    /**
     *
     * @param user
     * @return
     */
    public Collection<Channel> removeUser(final ChannelUser user) {
        mServer.removeUser(user);
        return user.getChannels();
    }

    /**
     *
     * @param channel
     * @return
     */
    public Collection<ChannelUser> removeChannel(final Channel channel) {
        mServer.getUser().getChannels().remove(channel);
        return channel.getUsers();
    }

    /**
     *
     * @param channel
     * @param user
     * @param userLevel
     */
    public void addUserToChannel(final Channel channel, final ChannelUser user,
            final UserLevel userLevel) {
        channel.addUser(user, userLevel);
    }

    /**
     *
     * @param channel
     * @param user
     * @param userLevel
     */
    public void addChannelToUser(final Channel channel, final ChannelUser user,
            final UserLevel userLevel) {
        user.addChannel(channel, userLevel);

        // Also remember to add the user to the global list
        mServer.addUser(user);
    }

    /**
     *
     * @param channel
     * @param user
     */
    public void removeUserFromChannel(Channel channel, ChannelUser user) {
        channel.removeUser(user);
    }

    /**
     *
     * @param channel
     * @param user
     */
    public void removeChannelFromUser(final Channel channel, final ChannelUser user) {
        final Collection<Channel> setOfChannels = user.getChannels();
        // The app user check is to make sure that the app user isn't removed from the list of
        // users
        if (setOfChannels.size() > 1 || user instanceof AppUser) {
            user.removeChannel(channel);
        } else {
            mServer.removeUser(user);
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public Channel getChannel(final String name) {
        for (final Channel channel : mServer.getUser().getChannels()) {
            // Channel names have to unique disregarding case - not having ignore-case here leads
            // to null channels when the channel does actually exist
            if (name.equalsIgnoreCase(channel.getName())) {
                return channel;
            }
        }
        return null;
    }

    /**
     *
     * @param rawSource
     * @return
     */
    public ChannelUser getUserFromRaw(final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        return getNonNullUser(nick);
    }

    /**
     *
     * @param nick
     * @return
     */
    public ChannelUser getUser(final String nick) {
        for (final ChannelUser user : mServer.getUsers()) {
            if (nick.equals(user.getNick().getNickAsString())) {
                return user;
            }
        }
        return null;
    }

    /**
     *
     * @param nick
     * @return
     */
    public ChannelUser getNonNullUser(final String nick) {
        final ChannelUser user = getUser(nick);
        return user != null ? user : new ChannelUser(nick);
    }

    public Channel getNewChannel(final String channelName) {
        return new Channel(mServer, channelName);
    }

    public Collection<QueryUser> getQueryUsers() {
        return mQueryUsers;
    }

    public QueryUser getQueryUser(final String nick) {
        for (final QueryUser user : mQueryUsers) {
            if (nick.equals(user.getNick().getNickAsString())) {
                return user;
            }
        }
        return null;
    }

    public void addQueryUser(final String nick, final String message, final boolean action,
            final boolean userSent) {
        final QueryUser user = new QueryUser(nick, mServer, message, action, userSent);
        mQueryUsers.add(user);
    }

    public void removeQueryUser(final QueryUser user) {
        mQueryUsers.remove(user);
    }

    public void onConnectionTerminated() {
        mServer.getUsers().clear();
        /*
        final Iterator<ChannelUser> iterator = mUserToChannelMap.keySet().iterator();
        while (iterator.hasNext()) {
            final ChannelUser user = iterator.next();
            if (user instanceof AppUser) {
                continue;
            }
            iterator.remove();
        }
        mChannelToUserMap.clear();

        final AppUser appUser = mServer.getUser();
        if (appUser == null) {
            return;
        }
        final Collection<Channel> channelSet = appUser.getChannels();
        for (final Channel channel : channelSet) {
            addUser(appUser, channel);
        }
        */
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