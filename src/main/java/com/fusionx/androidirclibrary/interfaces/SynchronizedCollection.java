package com.fusionx.androidirclibrary.interfaces;

import java.util.Collection;

public interface SynchronizedCollection<T> extends Collection<T> {

    public Object getLock();
}