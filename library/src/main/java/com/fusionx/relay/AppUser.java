package com.fusionx.relay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AppUser extends ChannelUser {

    private final List<PrivateMessageUser> mPrivateMessages;

    public AppUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);
        userChannelInterface.putAppUser(this);

        mPrivateMessages = new ArrayList<>();
    }

    public void createPrivateMessage(final PrivateMessageUser user) {
        mPrivateMessages.add(user);
    }

    public void closePrivateMessage(final PrivateMessageUser user) {
        mPrivateMessages.remove(user);
    }

    public boolean isPrivateMessageOpen(final PrivateMessageUser user) {
        return mPrivateMessages.contains(user);
    }

    public Collection<PrivateMessageUser> getPrivateMessages() {
        return mPrivateMessages;
    }

    @Override
    public Set<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
    }

    public Collection<String> getChannelList() {
        final Collection<String> channelList = new LinkedHashSet<>();
        for (Channel channel : getChannels()) {
            channelList.add(channel.getName());
        }
        return channelList;
    }

    // Two app users are only equal if and only if they belong to the same server
    @Override
    public boolean equals(final Object o) {
        if (o instanceof AppUser) {
            final AppUser us = ((AppUser) o);
            return us.getNick().equals(mNick) && us.mServer.equals(mServer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (mNick.hashCode() * 31) + mServer.hashCode();
    }
}