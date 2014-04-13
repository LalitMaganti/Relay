package com.fusionx.relay.communication;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.WorldUserEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.misc.InterfaceHolders;

import de.greenrobot.event.EventBus;

public class ServerEventBus {

    private final Server mServer;

    private final EventBus mBus;

    public ServerEventBus(final Server server) {
        mBus = new EventBus();
        mServer = server;
    }

    public void postAndStoreEvent(final ServerEvent event) {
        mServer.onServerEvent(event);
        mBus.post(event);
    }

    public void postAndStoreEvent(final ChannelEvent event, final Channel channel) {
        if (!WorldUserEvent.sUserListChangeEvents.contains(event) || InterfaceHolders
                .getPreferences().shouldLogUserListChanges()) {
            channel.onChannelEvent(event);
        }
        mBus.post(event);
    }

    public void postAndStoreEvent(final UserEvent event, final PrivateMessageUser user) {
        user.onUserEvent(event);
        mBus.post(event);
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