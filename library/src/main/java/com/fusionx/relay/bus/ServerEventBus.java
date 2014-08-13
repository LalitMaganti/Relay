package com.fusionx.relay.bus;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.query.QueryEvent;
import com.fusionx.relay.event.server.ServerEvent;

import de.greenrobot.event.EventBus;

public class ServerEventBus {

    private final RelayServer mServer;

    private final EventBus mBus;

    public ServerEventBus(final RelayServer server) {
        mBus = new EventBus();
        mServer = server;
    }

    public void postAndStoreEvent(final ServerEvent event) {
        mServer.onServerEvent(event);
        post(event);
    }

    public void postAndStoreEvent(final ChannelEvent event, final RelayChannel channel) {
        channel.onChannelEvent(event);
        post(event);
    }

    public void postAndStoreEvent(final QueryEvent event, final RelayQueryUser user) {
        user.onUserEvent(event);
        post(event);
    }

    public void register(final Object object) {
        mBus.register(object);
    }

    public void register(final Object object, final int priority) {
        mBus.register(object, priority);
    }

    public void unregister(final Object object) {
        mBus.unregister(object);
    }

    public void post(final Object event) {
        mBus.post(event);
    }
}