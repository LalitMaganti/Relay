package com.fusionx.relay.bus;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.RelayUserChannelInterface;
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
import com.fusionx.relay.event.query.QueryEvent;
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

    private final RelayUserChannelInterface mUserChannelInterface;

    public ServerCallBus(final RelayServer server, final Handler callHandler) {
        mServer = server;
        mUserChannelInterface = server.getUserChannelInterface();

        mCallHandler = callHandler;

        mBus = new Bus(ThreadEnforcer.ANY);
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

        sendSelfEventToQueryUser(user -> new QueryMessageSelfEvent(user, mServer.getUser(),
                message), nick, message);
    }

    public void sendActionToQueryUser(final String nick, final String action) {
        if (Utils.isNotEmpty(action)) {
            post(new PrivateActionCall(nick, action));
        }

        sendSelfEventToQueryUser(user -> new QueryActionSelfEvent(user, mServer.getUser(),
                action), nick, action);
    }

    private void sendSelfEventToQueryUser(final Function<RelayQueryUser, QueryEvent> function,
            final String nick, final String message) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        if (optional.isPresent()) {
            final RelayQueryUser user = optional.get();
            mServer.getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(user));

            if (Utils.isNotEmpty(message)) {
                mServer.getServerEventBus().postAndStoreEvent(function.apply(user), user);
            }
        } else {
            final RelayQueryUser user = mUserChannelInterface
                    .addQueryUser(nick, message, true, true);
            mServer.getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(user));
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
        mUserChannelInterface.removeQueryUser(user);

        if (InterfaceHolders.getPreferences().isSelfEventHidden()) {
            return;
        }
        final ServerEvent event = new PrivateMessageClosedEvent(user);
        mServer.getServerEventBus().postAndStoreEvent(event);
    }

    public void sendJoin(final String channelName) {
        post(new ChannelJoinCall(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new ChannelMessageCall(channelName, message));

        sendChannelSelfMessage(channel -> new ChannelMessageEvent(channel, message,
                mServer.getUser()), channelName);
    }

    public void sendActionToChannel(final String channelName, final String action) {
        post(new ChannelActionCall(channelName, action));

        sendChannelSelfMessage(channel -> new ChannelActionEvent(channel, action,
                mServer.getUser()), channelName);
    }

    private void sendChannelSelfMessage(final Function<RelayChannel, ChannelEvent> function,
            final String channelName) {
        if (InterfaceHolders.getPreferences().isSelfEventHidden()) {
            return;
        }

        final Optional<RelayChannel> optional = mUserChannelInterface.getChannel(channelName);
        if (optional.isPresent()) {
            final RelayChannel channel = optional.get();
            mServer.getServerEventBus().postAndStoreEvent(function.apply(channel), channel);
        } else {
            // TODO - some sort of logging should be done here
        }
    }

    public void sendKick(final String channelName, final String nick, final String reason) {
        post(new ChannelKickCall(channelName, nick, reason));
    }

    public void sendTopic(final String channelName, final String newTopic) {
        post(new ChannelTopicCall(channelName, newTopic));
    }

    public void postImmediately(final QuitCall quitCall) {
        mBus.post(quitCall);
    }
}