package com.fusionx.androidirclibrary.collection;

import com.fusionx.androidirclibrary.ChannelUser;
import com.fusionx.androidirclibrary.interfaces.SynchronizedCollection;

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