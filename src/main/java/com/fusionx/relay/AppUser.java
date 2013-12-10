package com.fusionx.relay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AppUser extends ChannelUser {

    private final List<PrivateMessageUser> mPrivateMessages;

    public AppUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);
        userChannelInterface.putAppUser(this);

        mPrivateMessages = new ArrayList<PrivateMessageUser>();
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

    public Iterator<PrivateMessageUser> getPrivateMessageIterator() {
        return mPrivateMessages.iterator();
    }

    @Override
    public Set<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AppUser) {
            final AppUser us = ((AppUser) o);
            return us.mNick.equals(mNick) && us.mServer.equals(mServer);
        } else {
            return false;
        }
    }
}