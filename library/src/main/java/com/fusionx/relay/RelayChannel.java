package com.fusionx.relay;

import com.google.common.collect.ImmutableList;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.event.channel.ChannelEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelayChannel implements Channel {

    // As set out in RFC2812
    private final static ImmutableList<Character> CHANNEL_PREFIXES = ImmutableList.of('#', '&',
            '+', '!');

    private final Server mServer;

    private final String mChannelName;

    private final Set<RelayChannelUser> mUsers;

    private final EnumMap<UserLevel, Integer> mNumberOfUsers;

    private final List<ChannelEvent> mBuffer;

    RelayChannel(final Server server, final String channelName) {
        mServer = server;
        mChannelName = channelName;

        mBuffer = new ArrayList<>();
        mNumberOfUsers = new EnumMap<>(UserLevel.class);
        mUsers = new HashSet<>();

        clearInternalData();
    }

    /**
     * Returns whether a string is a channel name based on the first character of the string
     *
     * @param firstCharacter the first character of the string that is to be tested
     * @return whether the character can be one at the start of a channel
     */
    public static boolean isChannelPrefix(char firstCharacter) {
        return CHANNEL_PREFIXES.contains(firstCharacter);
    }

    public void clearInternalData() {
        // Clear the user count
        for (final UserLevel levelEnum : UserLevel.values()) {
            mNumberOfUsers.put(levelEnum, 0);
        }
        // Clear the list of users
        mUsers.clear();
    }

    /**
     * Called when a event occurs on this channel
     *
     * @param event the event which occured
     */
    public void onChannelEvent(final ChannelEvent event) {
        mBuffer.add(event);
    }

    // User stuff starts here

    /**
     * Returns a list of all the users currently in the channel
     *
     * @return list of users currently in the channel
     */
    @Override
    public Collection<RelayChannelUser> getUsers() {
        return mUsers;
    }

    /**
     *
     *
     * @param user
     * @param userLevel
     */
    void addUser(final RelayChannelUser user, final UserLevel userLevel) {
        if (mUsers.contains(user)) {
            // TODO - this is invalid - need to track it down if it does happen
        }

        mUsers.add(user);
        incrementUserType(userLevel);
    }

    /**
     *
     * @param user
     */
    void removeUser(final RelayChannelUser user) {
        if (!mUsers.contains(user)) {
            // TODO - this is invalid - need to track it down if it does happen
        }
        mUsers.remove(user);
        decrementUserType(user.getChannelPrivileges(this));
    }

    /**
     * Increments the type of user in the channel by 1
     *
     * @param userLevel the type of user
     */
    void incrementUserType(final UserLevel userLevel) {
        if (userLevel == UserLevel.NONE) {
            return;
        }
        synchronized (mNumberOfUsers) {
            Integer users = mNumberOfUsers.get(userLevel);
            mNumberOfUsers.put(userLevel, ++users);
        }
    }

    /**
     * Decrements the type of user in the channel by 1
     *
     * @param userLevel the type of user
     */
    void decrementUserType(final UserLevel userLevel) {
        if (userLevel == UserLevel.NONE) {
            return;
        }
        synchronized (mNumberOfUsers) {
            Integer users = mNumberOfUsers.get(userLevel);
            mNumberOfUsers.put(userLevel, --users);
        }
    }

    /**
     * Gets the number of users in the channel
     *
     * @return the number of users in the channel
     */
    @Override
    public int getUserCount() {
        final Collection<RelayChannelUser> users = getUsers();
        return users != null ? users.size() : 0;
    }

    /**
     * Gets the number of users of a specific level in the channel
     *
     * @param userLevel - the level to get
     * @return the number of users of this level
     */
    @Override
    public int getNumberOfUsersType(final UserLevel userLevel) {
        synchronized (mNumberOfUsers) {
            if (userLevel == UserLevel.NONE) {
                int normalUsers = getUserCount();
                for (UserLevel levelEnum : UserLevel.values()) {
                    normalUsers -= mNumberOfUsers.get(levelEnum);
                }
                return normalUsers;
            }
            return mNumberOfUsers.get(userLevel);
        }
    }

    // Getters and setters

    /**
     * Gets the name of the channel
     *
     * @return the name of the channel
     */
    @Override
    public String getName() {
        return mChannelName;
    }

    /**
     * Gets the buffer of the channel - the events which occured since this channel was created
     *
     * @return a list of the events
     */
    @Override
    public List<ChannelEvent> getBuffer() {
        return mBuffer;
    }

    /**
     * Returns the id of the channel which is simply its name
     *
     * @return the id (name) of the channel
     */
    @Override
    public String getId() {
        return mChannelName;
    }

    /**
     * Returns the server this channel is attached to
     *
     * @return the server this channel belongs to
     */
    @Override
    public Server getServer() {
        return mServer;
    }

    /*
     * A channel is equal to another if the servers are equal and if the channel's names are
     * equal regardless of case
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Channel)) {
            return false;
        }
        final RelayChannel otherChannel = (RelayChannel) o;
        return otherChannel.getServer().equals(getServer())
                && otherChannel.getName().equalsIgnoreCase(mChannelName);
    }

    /*
     * The hashcode of a channel can simply be the same as that of its name
     */
    @Override
    public int hashCode() {
        return mChannelName.toLowerCase().hashCode();
    }

    /*
     * A channel's string representation is simply its name
     */
    @Override
    public String toString() {
        return mChannelName;
    }

}