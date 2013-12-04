/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary;

import com.fusionx.androidirclibrary.collection.UserListTreeSet;
import com.fusionx.androidirclibrary.constants.UserLevelEnum;
import com.fusionx.androidirclibrary.event.ChannelEvent;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.fusionx.androidirclibrary.writers.ChannelWriter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Channel {

    /**
     * Name of the channel
     */
    private final String mName;

    private final ChannelWriter mWriter;

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
        mWriter = new ChannelWriter(userChannelInterface.getOutputStream(), this);
        mUserChannelInterface = userChannelInterface;
        mBuffer = new ArrayList<Message>();
        mNumberOfUsers = new EnumMap<UserLevelEnum, Integer>(UserLevelEnum.class);

        for (UserLevelEnum levelEnum : UserLevelEnum.values()) {
            mNumberOfUsers.put(levelEnum, 0);
        }

        final String message = InterfaceHolders.getEventResponses().getJoinMessage
                (mUserChannelInterface.getServer().getUser().getColorfulNick());
        mBuffer.add(new Message(message));
    }

    public UserListTreeSet getUsers() {
        return mUserChannelInterface.getAllUsersInChannel(this);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Channel && ((Channel) o).mName.equals(mName);
    }

    public void onChannelEvent(final ChannelEvent event) {
        //if ((!event.userListChanged || !AppPreferences.hideUserMessages) && StringUtils
        //        .isNotEmpty(event.message)) {
        mBuffer.add(new Message(event.message));
        //}
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

    // Getters and setters
    public String getName() {
        return mName;
    }

    public ChannelWriter getWriter() {
        return mWriter;
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