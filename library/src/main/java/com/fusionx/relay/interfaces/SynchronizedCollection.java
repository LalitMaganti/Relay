package com.fusionx.relay.interfaces;

import java.util.Collection;

public interface SynchronizedCollection<T> extends Collection<T> {

    public Object getLock();
}