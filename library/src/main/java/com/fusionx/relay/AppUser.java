package com.fusionx.relay;

import java.util.Collection;

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