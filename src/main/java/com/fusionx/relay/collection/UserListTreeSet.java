package com.fusionx.relay.collection;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.interfaces.SynchronizedCollection;

import java.util.Comparator;

public class UserListTreeSet extends UpdateableTreeSet<ChannelUser> implements
        SynchronizedCollection<ChannelUser> {

    public UserListTreeSet(Comparator<ChannelUser> comparator) {
        super(comparator);
    }

    private final Object mLock = new Object();

    @Override
    public Object getLock() {
        return mLock;
    }
}