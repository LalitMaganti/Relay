package com.fusionx.relay.bus;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.call.channel.ChannelActionCall;
import com.fusionx.relay.call.channel.ChannelJoinCall;
import com.fusionx.relay.call.channel.ChannelKickCall;
import com.fusionx.relay.call.channel.ChannelMessageCall;
import com.fusionx.relay.call.channel.ChannelPartCall;
import com.fusionx.relay.call.channel.ChannelTopicCall;
import com.fusionx.relay.call.server.ModeCall;
import com.fusionx.relay.call.server.NickChangeCall;
import com.fusionx.relay.call.server.QuitCall;
import com.fusionx.relay.call.server.RawCall;
import com.fusionx.relay.call.server.WhoisCall;
import com.fusionx.relay.call.user.PrivateActionCall;
import com.fusionx.relay.call.user.PrivateMessageCall;
import com.fusionx.relay.event.channel.ChannelActionEvent;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelMessageEvent;
import com.fusionx.relay.event.query.QueryActionSelfEvent;
import com.fusionx.relay.event.query.QueryMessageSelfEvent;
import com.fusionx.relay.event.server.NewPrivateMessageEvent;
import com.fusionx.relay.event.server.PrivateMessageClosedEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.RawWriter;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.os.Handler;

import java.util.Iterator;
import java.util.Set;

import gnu.trove.set.hash.THashSet;

public class ServerCallBus {

    private final Set<RawWriter> mRawWriterSet = new THashSet<>();

    private final RelayServer mServer;

    private final Handler mCallHandler;

    private final Bus mBus;

    public ServerCallBus(final RelayServer server, final Handler callHandler) {
        mBus = new Bus(ThreadEnforcer.ANY);
        mServer = server;
        mCallHandler = callHandler;
    }

    public void register(final RawWriter rawWriter) {
        mBus.register(rawWriter);
        mRawWriterSet.add(rawWriter);
    }

    public void onConnectionTerminated() {
        for (final Iterator<RawWriter> iterator = mRawWriterSet.iterator(); iterator.hasNext(); ) {
            final RawWriter writer = iterator.next();
            iterator.remove();
            mBus.unregister(writer);
        }
    }

    public void post(final Object event) {
        mCallHandler.post(() -> mBus.post(event));
    }

    public void sendMode(final String channelName, final String destination, final String mode) {
        post(new ModeCall(channelName, destination, mode));
    }

    public void sendUserWhois(final String nick) {
        post(new WhoisCall(nick));
    }

    public void sendRawLine(final String rawLine) {
        post(new RawCall(rawLine));
    }

    public void sendMessageToQueryUser(final String nick, final String message) {
        if (Utils.isNotEmpty(message)) {
            post(new PrivateMessageCall(nick, message));
        }

        final RelayQueryUser user = getServer().getUserChannelInterface().getQueryUser(nick);
        if (user == null) {
            getServer().getUserChannelInterface().addQueryUser(nick, message, false,
                    true);
            getServer().getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(nick));
        } else if (Utils.isNotEmpty(message)) {
            getServer().getServerEventBus().postAndStoreEvent(new QueryMessageSelfEvent(user,
                    getServer().getUser(), message), user);
        }
    }

    public void sendActionToQueryUser(final String nick, final String action) {
        if (Utils.isNotEmpty(action)) {
            post(new PrivateActionCall(nick, action));
        }

        final RelayQueryUser user = getServer().getUserChannelInterface().getQueryUser(nick);
        if (user == null) {
            getServer().getUserChannelInterface().addQueryUser(nick, action, true,
                    true);
            getServer().getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(nick));
        } else {
            getServer().getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(nick));
            if (Utils.isNotEmpty(action)) {
                getServer().getServerEventBus().postAndStoreEvent(new QueryActionSelfEvent(user,
                        getServer().getUser(), action), user);
            }
        }
    }

    public void sendNickChange(final String newNick) {
        post(new NickChangeCall(newNick));
    }

    public void sendPart(final String channelName) {
        post(new ChannelPartCall(channelName, InterfaceHolders.getPreferences().getPartReason()));
    }

    public void sendCloseQuery(final QueryUser rawUser) {
        if (!(rawUser instanceof RelayQueryUser)) {
            // TODO - this is invalid and unexpected. What should be done here?
            return;
        }
        final RelayQueryUser user = (RelayQueryUser) rawUser;
        getServer().getUserChannelInterface().removeQueryUser(user);

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final ServerEvent event = new PrivateMessageClosedEvent(user);
            getServer().getServerEventBus().postAndStoreEvent(event);
        }
    }

    public void sendJoin(final String channelName) {
        post(new ChannelJoinCall(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new ChannelMessageCall(channelName, message));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final RelayChannel channel = getServer().getUserChannelInterface()
                    .getChannel(channelName);
            final ChannelEvent event = new ChannelMessageEvent(channel, message,
                    getServer().getUser());
            getServer().getServerEventBus().postAndStoreEvent(event, channel);
        }
    }

    public void sendActionToChannel(final String channelName, final String action) {
        post(new ChannelActionCall(channelName, action));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final RelayChannel channel = getServer().getUserChannelInterface()
                    .getChannel(channelName);
            final ChannelEvent event = new ChannelActionEvent(channel, action,
                    getServer().getUser());
            getServer().getServerEventBus().postAndStoreEvent(event, channel);
        }
    }

    public void sendKick(final String channelName, final String nick, final String reason) {
        post(new ChannelKickCall(channelName, nick, reason));
    }

    public void sendTopic(final String channelName, final String newTopic) {
        post(new ChannelTopicCall(channelName, newTopic));
    }

    private RelayServer getServer() {
        return mServer;
    }

    public void postImmediately(final QuitCall quitCall) {
        mBus.post(quitCall);
    }
}