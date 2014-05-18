package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

public final class UserChannelInterface {

    private final Map<WorldUser, Collection<Channel>> mUserToChannelMap;

    private final Map<Channel, Collection<WorldUser>> mChannelToUserMap;

    private final Collection<QueryUser> mQueryUsers;

    private final Server mServer;

    private Set<String> mUserIgnoreList;

    public UserChannelInterface(final Server server) {
        mServer = server;

        mUserToChannelMap = new THashMap<>();
        mChannelToUserMap = new THashMap<>();
        mQueryUsers = new TLinkedHashSet<>();
        mUserIgnoreList = new THashSet<>();
    }

    public synchronized void coupleUserAndChannel(final WorldUser user, final Channel channel) {
        coupleUserAndChannel(user, channel, UserLevel.NONE);
    }

    public synchronized void coupleUserAndChannel(final WorldUser user, final Channel channel,
            final UserLevel userLevel) {
        user.onModeChanged(channel, userLevel);
        addChannelToUser(user, channel);
        addUserToChannel(user, channel);

        channel.onIncrementUserType(userLevel);
    }

    public synchronized void decoupleUserAndChannel(final WorldUser user, final Channel channel) {
        removeUserFromChannel(channel, user);
        removeChannelFromUser(channel, user);
        user.onRemove(channel);
    }

    public void removeUserFromChannel(final Channel channel, final WorldUser user) {
        channel.onDecrementUserType(user.getChannelPrivileges(channel));

        final Collection<WorldUser> setOfUsers = mChannelToUserMap.get(channel);
        if (setOfUsers.size() > 1) {
            setOfUsers.remove(user);
        } else {
            mChannelToUserMap.remove(channel);
        }
    }

    public synchronized Collection<Channel> removeUser(final WorldUser user) {
        return mUserToChannelMap.remove(user);
    }

    public synchronized void removeChannel(final Channel channel) {
        for (final WorldUser user : mChannelToUserMap.remove(channel)) {
            user.onRemove(channel);
            removeChannelFromUser(channel, user);
        }
    }

    public synchronized WorldUser getUserFromRaw(final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        return getUser(nick);
    }

    public synchronized WorldUser getUser(final String nick) {
        final WorldUser user = getUserIfExists(nick);
        return user != null ? user : new WorldUser(nick, this);
    }

    public synchronized WorldUser getUserIfExists(final String nick) {
        for (final WorldUser user : mUserToChannelMap.keySet()) {
            if (nick.equals(user.getNick().getNickAsString())) {
                return user;
            }
        }
        return null;
    }

    public synchronized Channel getChannel(final String name) {
        for (final Channel channel : mChannelToUserMap.keySet()) {
            // Channel names have to unique disregarding case - not having ignore-case here leads
            // to null channels when the channel does actually exist
            if (name.equalsIgnoreCase(channel.getName())) {
                return channel;
            }
        }
        return null;
    }

    public Channel getNewChannel(final String channelName) {
        return new Channel(channelName, this);
    }

    public QueryUser getQueryUser(final String nick) {
        for (final QueryUser user : mQueryUsers) {
            if (nick.equals(user.getNick().getNickAsString())) {
                return user;
            }
        }
        return null;
    }

    public Collection<QueryUser> getQueryUsers() {
        return mQueryUsers;
    }

    public void addNewPrivateMessageUser(final String nick, final String message,
            final boolean action, final boolean userSent) {
        final QueryUser user = new QueryUser(nick, this, message, action,
                userSent);
        mQueryUsers.add(user);
    }

    public void removePrivateMessageUser(final QueryUser user) {
        mQueryUsers.remove(user);
    }

    public void onConnectionTerminated() {
        final Iterator<WorldUser> iterator = mUserToChannelMap.keySet().iterator();
        while (iterator.hasNext()) {
            final WorldUser user = iterator.next();
            if (!(user instanceof AppUser)) {
                iterator.remove();
            }
        }
        mChannelToUserMap.clear();

        if (mServer.getUser() != null) {
            final Collection<Channel> channelSet = mServer.getUser().getChannels();
            for (final Channel channel : channelSet) {
                addUserToChannel(mServer.getUser(), channel);
            }
        }
    }

    public boolean shouldIgnoreUser(final String userNick) {
        return mUserIgnoreList.contains(userNick);
    }

    void removeChannelFromUser(final Channel channel, final WorldUser user) {
        final Collection<Channel> setOfChannels = mUserToChannelMap.get(user);
        // The app user check is to make sure that the list of channels returned for the app user
        // is never null
        if (setOfChannels.size() > 1 || user instanceof AppUser) {
            setOfChannels.remove(channel);
        } else {
            mUserToChannelMap.remove(user);
        }
    }

    synchronized Collection<Channel> getAllChannelsInUser(final WorldUser user) {
        return mUserToChannelMap.get(user);
    }

    synchronized Collection<WorldUser> getAllUsersInChannel(final Channel channel) {
        return mChannelToUserMap.get(channel);
    }

    synchronized void putAppUser(final AppUser user) {
        mUserToChannelMap.put(user, new TLinkedHashSet<Channel>());
    }

    // Getters and setters
    void updateIgnoreList(final Collection<String> userIgnoreList) {
        if (userIgnoreList != null) {
            mUserIgnoreList = new THashSet<>(userIgnoreList);
        }
    }

    Server getServer() {
        return mServer;
    }

    private synchronized void addUserToChannel(final WorldUser user, final Channel channel) {
        Collection<WorldUser> setOfUsers = mChannelToUserMap.get(channel);
        if (setOfUsers == null) {
            setOfUsers = new THashSet<>();
            mChannelToUserMap.put(channel, setOfUsers);
        }
        setOfUsers.add(user);
    }

    private synchronized void addChannelToUser(final WorldUser user, final Channel channel) {
        Collection<Channel> setOfChannels = mUserToChannelMap.get(user);
        if (setOfChannels == null) {
            // Linked hash set used to preserve insertion order - so that the channels are always
            // displayed to the user in the order they were joined
            setOfChannels = new TLinkedHashSet<>();
            mUserToChannelMap.put(user, setOfChannels);
        }
        setOfChannels.add(channel);
    }
}