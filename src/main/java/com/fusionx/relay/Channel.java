package com.fusionx.relay;

import com.google.common.collect.ImmutableList;

import com.fusionx.relay.collection.UserListTreeSet;
import com.fusionx.relay.constants.UserLevelEnum;
import com.fusionx.relay.event.ChannelEvent;
import com.fusionx.relay.misc.InterfaceHolders;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Channel {

    // Static stuff
    private final static ImmutableList<Character> channelPrefixes = ImmutableList.of('#', '&',
            '+', '!');

    /**
     * Name of the channel
     */
    private final String mName;

    private final UserChannelInterface mUserChannelInterface;

    private final List<Message> mBuffer;

    private final EnumMap<UserLevelEnum, Integer> mNumberOfUsers;

    /**
     * Topic of the channel
     */
    private String mTopic;

    private boolean mCached;

    Channel(final String channelName, final UserChannelInterface
            userChannelInterface) {
        mName = channelName;
        mUserChannelInterface = userChannelInterface;
        mBuffer = new ArrayList<Message>();
        mNumberOfUsers = new EnumMap<UserLevelEnum, Integer>(UserLevelEnum.class);

        for (final UserLevelEnum levelEnum : UserLevelEnum.values()) {
            mNumberOfUsers.put(levelEnum, 0);
        }

        final String userNick = mUserChannelInterface.getServer().getUser().getColorfulNick();
        final String message = InterfaceHolders.getEventResponses().getJoinMessage(userNick);
        mBuffer.add(new Message(message));
    }

    public static boolean isChannelPrefix(char firstCharacter) {
        return channelPrefixes.contains(firstCharacter);
    }

    /**
     * Returns a list of all the users currently in the channel
     *
     * @return list of users currently in the channel
     */
    public UserListTreeSet getUsers() {
        return mUserChannelInterface.getAllUsersInChannel(this);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Channel && ((Channel) o).mName.equals(mName);
    }

    public void onChannelEvent(final ChannelEvent event) {
        if ((!event.userListChanged || InterfaceHolders.getPreferences()
                .shouldLogUserListChanges()) && StringUtils.isNotEmpty(event.message)) {
            mBuffer.add(new Message(event.message));
        }
    }

    public int getNumberOfUsers() {
        if (getUsers() != null) {
            return getUsers().size();
        } else {
            return 0;
        }
    }

    public void onIncrementUserType(final UserLevelEnum userLevelEnum) {
        if (userLevelEnum != UserLevelEnum.NONE) {
            synchronized (mNumberOfUsers) {
                Integer users = mNumberOfUsers.get(userLevelEnum);
                mNumberOfUsers.put(userLevelEnum, ++users);
            }
        }
    }

    public void onDecrementUserType(final UserLevelEnum userLevelEnum) {
        if (userLevelEnum != UserLevelEnum.NONE) {
            synchronized (mNumberOfUsers) {
                Integer users = mNumberOfUsers.get(userLevelEnum);
                mNumberOfUsers.put(userLevelEnum, --users);
            }
        }
    }

    public int getNumberOfUsersType(final UserLevelEnum userLevelEnum) {
        synchronized (mNumberOfUsers) {
            if (userLevelEnum != UserLevelEnum.NONE) {
                return mNumberOfUsers.get(userLevelEnum);
            } else {
                int normalUsers = getNumberOfUsers();
                for (UserLevelEnum levelEnum : UserLevelEnum.values()) {
                    normalUsers -= mNumberOfUsers.get(levelEnum);
                }
                return normalUsers;
            }
        }
    }

    @Override
    public String toString() {
        return mName;
    }

    // Getters and setters
    public String getName() {
        return mName;
    }

    public List<Message> getBuffer() {
        return mBuffer;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String mTopic) {
        this.mTopic = mTopic;
    }

    public boolean isCached() {
        return mCached;
    }

    public void setCached(boolean cached) {
        mCached = cached;
    }
}