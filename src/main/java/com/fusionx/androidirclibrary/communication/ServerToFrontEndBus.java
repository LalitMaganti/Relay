package com.fusionx.androidirclibrary.communication;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.os.Handler;
import android.os.Looper;

class ServerToFrontEndBus extends Bus {

    private final Handler mMainThread = new Handler(Looper.getMainLooper());

    private final MessageSender mSender;

    private int mRegisteredCount;

    public ServerToFrontEndBus(final MessageSender sender) {
        super(ThreadEnforcer.ANY);
        mSender = sender;
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mMainThread.post(new Runnable() {
                @Override
                public void run() {
                    ServerToFrontEndBus.super.post(event);
                }
            });
        }
    }

    @Override
    public void register(Object object) {
        super.register(object);

        ++mRegisteredCount;
    }

    @Override
    public void unregister(Object object) {
        super.unregister(object);

        --mRegisteredCount;
        if (mRegisteredCount == 0) {
            mSender.removeSender();
        }
    }
}