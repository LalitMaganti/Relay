package com.fusionx.relay;

import java.util.Collection;
import java.util.Set;

import gnu.trove.set.hash.TLinkedHashSet;

public class AppUser extends WorldUser {

    public AppUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);

        userChannelInterface.putAppUser(this);
    }

    @Override
    public Collection<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
    }
}