package com.fusionx.relay.communication;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.WorldUserEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.os.Handler;
import android.os.Looper;

public class ServerEventBus {

    private final Server mServer;

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private final Bus mBus;

    public ServerEventBus(final Server server) {
        mBus = new Bus(ThreadEnforcer.ANY);
        mServer = server;
    }

    public void postAndStoreEvent(final ServerEvent event) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mServer.onServerEvent(event);
                mBus.post(event);
            }
        });
    }

    public void postAndStoreEvent(final ChannelEvent event, final Channel channel) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!(event instanceof WorldUserEvent) || InterfaceHolders.getPreferences()
                        .shouldLogUserListChanges()) {
                    channel.onChannelEvent(event);
                }
                mBus.post(event);
            }
        });
    }

    public void postAndStoreEvent(final UserEvent event, final PrivateMessageUser user) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                user.onUserEvent(event);
                mBus.post(event);
            }
        });
    }

    public void register(final Object object) {
        mBus.register(object);
    }

    public void unregister(final Object object) {
        mBus.unregister(object);
    }

    public void post(final Object event) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mBus.post(event);
            }
        });
    }
}