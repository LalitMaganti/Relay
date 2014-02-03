package com.fusionx.relay;

import java.util.Collection;
import java.util.Set;

import gnu.trove.set.hash.TLinkedHashSet;

public class AppUser extends WorldUser {

    private final Set<ChannelSnapshot> mChannelSnapshots;

    public AppUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);

        mChannelSnapshots = new TLinkedHashSet<>();
        userChannelInterface.putAppUser(this);
    }

    @Override
    public Collection<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
    }

    public void onDisconnect() {
        if (mChannelSnapshots.isEmpty()) {
            for (Channel channel : getChannels()) {
                mChannelSnapshots.add(new ChannelSnapshot(channel));
            }
        }
    }

    public Set<ChannelSnapshot> getChannelSnapshots() {
        return mChannelSnapshots;
    }

    public ChannelSnapshot getChannelSnapshot(final String channelName) {
        for (ChannelSnapshot snapshot : mChannelSnapshots) {
            if (snapshot.getName().equals(channelName)) {
                return snapshot;
            }
        }
        return null;
    }
}