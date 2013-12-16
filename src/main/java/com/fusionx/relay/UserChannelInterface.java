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
        mUserToChannelMap = new HashMap<ChannelUser, LinkedHashSet<Channel>>();
        mChannelToUserMap = new HashMap<Channel, UserListTreeSet>();
    }

    public synchronized void coupleUserAndChannel(final ChannelUser user,
            final Channel channel) {
        user.onJoin(channel);
        addChannelToUser(user, channel);
        addUserToChannel(user, channel);
    }

    public synchronized void addChannelToUser(final ChannelUser user,
            final Channel channel) {
        LinkedHashSet<Channel> list = mUserToChannelMap.get(user);
        if (list == null) {
            list = new LinkedHashSet<Channel>();
            mUserToChannelMap.put(user, list);
        }
        list.add(channel);
    }

    private synchronized void addUserToChannel(final ChannelUser user,
            final Channel channel) {
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

        final Set<Channel> setOfChannels = mUserToChannelMap.get(user);
        if (setOfChannels != null) {
            setOfChannels.remove(channel);
            if (setOfChannels.isEmpty()) {
                mUserToChannelMap.remove(user);
            }
        }
        final UserListTreeSet setOfUsers = mChannelToUserMap.get(channel);
        if (setOfUsers != null) {
            synchronized (setOfUsers.getLock()) {
                setOfUsers.remove(user);
            }
            if (setOfUsers.isEmpty()) {
                mChannelToUserMap.remove(channel);
            }
        }
    }

    public synchronized Set<Channel> removeUser(final ChannelUser user) {
        final Set<Channel> removedSet = mUserToChannelMap.remove(user);
        if (removedSet != null) {
            for (final Channel channel : removedSet) {
                final UserListTreeSet set = mChannelToUserMap.get(channel);
                synchronized (set.getLock()) {
                    set.remove(user);
                }
            }
        }
        return removedSet;
    }

    public synchronized void removeChannel(final Channel channel) {
        for (final ChannelUser user : mChannelToUserMap.remove(channel)) {
            final LinkedHashSet<Channel> channelMap = mUserToChannelMap.get(user);
            if (channelMap != null) {
                channelMap.remove(channel);
                user.onRemove(channel);
                if (channelMap.isEmpty()) {
                    mUserToChannelMap.remove(user);
                }
            }
        }
    }

    synchronized UserListTreeSet getAllUsersInChannel(final Channel channel) {
        return mChannelToUserMap.get(channel);
    }

    synchronized LinkedHashSet<Channel> getAllChannelsInUser(final ChannelUser user) {
        return mUserToChannelMap.get(user);
    }

    public synchronized ChannelUser getUserFromRaw(final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        return getUser(nick);
    }

    public synchronized ChannelUser getUserIfExists(final String nick) {
        for (final ChannelUser user : mUserToChannelMap.keySet()) {
            if (nick.equals(user.getNick())) {
                return user;
            }
        }
        return null;
    }

    public synchronized ChannelUser getUser(final String nick) {
        return getUserIfExists(nick) != null ? getUserIfExists(nick) : new ChannelUser(nick, this);
    }

    public synchronized Channel getChannel(final String name) {
        return getChannelIfExists(name) != null ? getChannelIfExists(name) : new Channel(name,
                this);
    }

    public synchronized Channel getChannelIfExists(final String name) {
        for (final Channel channel : mChannelToUserMap.keySet()) {
            if (channel.getName().equals(name)) {
                return channel;
            }
        }
        return null;
    }

    synchronized void putAppUser(final AppUser user) {
        mUserToChannelMap.put(user, new LinkedHashSet<Channel>());
    }

    // Getters and setters
    Server getServer() {
        return mServer;
    }

    public void onCleanup() {
        mUserToChannelMap.clear();
        mChannelToUserMap.clear();
    }
}