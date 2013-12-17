package com.fusionx.relay;

import com.fusionx.relay.collection.UserListTreeSet;
import com.fusionx.relay.misc.IRCUserComparator;
import com.fusionx.relay.util.IRCUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class UserChannelInterface {

    private final Map<ChannelUser, LinkedHashSet<Channel>> mUserToChannelMap;

    private final Map<Channel, UserListTreeSet> mChannelToUserMap;

    private final Server mServer;

    public UserChannelInterface(final Server server) {
        mServer = server;
        mUserToChannelMap = new HashMap<>();
        mChannelToUserMap = new HashMap<>();
    }

    public synchronized void coupleUserAndChannel(final ChannelUser user, final Channel channel) {
        user.onJoin(channel);
        addChannelToUser(user, channel);
        addUserToChannel(user, channel);
    }

    public synchronized void addChannelToUser(final ChannelUser user, final Channel channel) {
        LinkedHashSet<Channel> list = mUserToChannelMap.get(user);
        if (list == null) {
            list = new LinkedHashSet<>();
            mUserToChannelMap.put(user, list);
        }
        list.add(channel);
    }

    private synchronized void addUserToChannel(final ChannelUser user, final Channel channel) {
        UserListTreeSet setOfUsers = mChannelToUserMap.get(channel);
        if (setOfUsers == null) {
            setOfUsers = new UserListTreeSet(new IRCUserComparator(channel));
            mChannelToUserMap.put(channel, setOfUsers);
        }
        synchronized (setOfUsers.getLock()) {
            setOfUsers.add(user);
        }
    }

    public synchronized void decoupleUserAndChannel(final ChannelUser user, final Channel channel) {
        user.onRemove(channel);
        removeChannelFromUser(channel, user);
        removeUserFromChannel(channel, user);
    }

    private void removeUserFromChannel(final Channel channel, final ChannelUser user) {
        final UserListTreeSet setOfUsers = mChannelToUserMap.get(channel);
        synchronized (setOfUsers.getLock()) {
            if (setOfUsers.size() > 1) {
                setOfUsers.remove(user);
            } else {
                mChannelToUserMap.remove(channel);
            }
        }
    }

    private void removeChannelFromUser(final Channel channel, final ChannelUser user) {
        final Set<Channel> setOfChannels = mUserToChannelMap.get(user);
        // The app user check is to make sure that the list of channels returned for the app user
        // is never null
        if (setOfChannels.size() > 1 || user instanceof AppUser) {
            setOfChannels.remove(channel);
        } else {
            mUserToChannelMap.remove(user);
        }
    }

    public synchronized Set<Channel> removeUser(final ChannelUser user) {
        final Set<Channel> removedSet = mUserToChannelMap.remove(user);
        for (final Channel channel : removedSet) {
            removeUserFromChannel(channel, user);
        }
        return removedSet;
    }

    public synchronized void removeChannel(final Channel channel) {
        for (final ChannelUser user : mChannelToUserMap.remove(channel)) {
            user.onRemove(channel);
            removeChannelFromUser(channel, user);
        }
    }

    synchronized UserListTreeSet getAllUsersInChannel(final Channel channel) {
        return mChannelToUserMap.get(channel);
    }

    synchronized Set<Channel> getAllChannelsInUser(final ChannelUser user) {
        return mUserToChannelMap.get(user);
    }

    public synchronized ChannelUser getUserFromRaw(final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        return getUser(nick);
    }

    public synchronized ChannelUser getUser(final String nick) {
        final ChannelUser user = getUserIfExists(nick);
        return user != null ? user : new ChannelUser(nick, this);
    }

    public synchronized Channel getChannel(final String name) {
        final Channel channel = getChannelIfExists(name);
        return channel != null ? channel : new Channel(name, this);
    }

    public synchronized ChannelUser getUserIfExists(final String nick) {
        for (final ChannelUser user : mUserToChannelMap.keySet()) {
            if (nick.equals(user.getNick())) {
                return user;
            }
        }
        return null;
    }

    public synchronized Channel getChannelIfExists(final String name) {
        for (final Channel channel : mChannelToUserMap.keySet()) {
            if (name.equals(channel.getName())) {
                return channel;
            }
        }
        return null;
    }

    synchronized void putAppUser(final AppUser user) {
        mUserToChannelMap.put(user, new LinkedHashSet<Channel>());
    }

    public void onCleanup() {
        mUserToChannelMap.clear();
        mChannelToUserMap.clear();
    }

    // Getters and setters
    Server getServer() {
        return mServer;
    }
}