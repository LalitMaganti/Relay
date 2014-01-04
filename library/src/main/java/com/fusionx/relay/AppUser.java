package com.fusionx.relay;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class AppUser extends WorldUser {

    public AppUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);

        userChannelInterface.putAppUser(this);
    }

    @Override
    public Collection<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
    }

    public Collection<String> getChannelList() {
        final Collection<String> channelList = new LinkedHashSet<>();
        for (Channel channel : getChannels()) {
            channelList.add(channel.getName());
        }
        return channelList;
    }
}