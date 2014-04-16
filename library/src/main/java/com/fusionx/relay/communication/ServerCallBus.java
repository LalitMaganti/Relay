package com.fusionx.relay.communication;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.call.ChannelActionCall;
import com.fusionx.relay.call.ChannelJoinCall;
import com.fusionx.relay.call.ChannelKickCall;
import com.fusionx.relay.call.ChannelMessageCall;
import com.fusionx.relay.call.ChannelPartCall;
import com.fusionx.relay.call.ModeCall;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.call.PrivateActionCall;
import com.fusionx.relay.call.PrivateMessageCall;
import com.fusionx.relay.call.RawCall;
import com.fusionx.relay.call.WhoisCall;
import com.fusionx.relay.event.server.NewPrivateMessage;
import com.fusionx.relay.event.channel.ActionEvent;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.MessageEvent;
import com.fusionx.relay.event.server.PrivateMessageClosedEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.event.user.PrivateActionEvent;
import com.fusionx.relay.event.user.PrivateMessageEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.RawWriter;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.os.Handler;

import java.util.Set;

import gnu.trove.set.hash.THashSet;

public class ServerCallBus {

    private final Set<RawWriter> mRawWriterSet = new THashSet<>();

    private final Server mServer;

    private final Handler mCallHandler;

    private Bus mBus;

    public ServerCallBus(final Server server, final Handler callHandler) {
        mBus = new Bus(ThreadEnforcer.ANY);
        mServer = server;
        mCallHandler = callHandler;
    }

    public void register(RawWriter rawWriter) {
        mBus.register(rawWriter);
        mRawWriterSet.add(rawWriter);
    }

    public void onDisconnect() {
        for (final RawWriter writer : mRawWriterSet) {
            mBus.unregister(writer);
        }
        mRawWriterSet.clear();
    }

    public void post(final Object event) {
        mCallHandler.post(new Runnable() {
            @Override
            public void run() {
                mBus.post(event);
            }
        });
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

    public void sendMessageToUser(final String nick, final String message) {
        if (Utils.isNotEmpty(message)) {
            post(new PrivateMessageCall(nick, message));
        }

        final PrivateMessageUser user = getServer().getUserChannelInterface()
                .getPrivateMessageUser(nick);
        if (user == null) {
            getServer().getUserChannelInterface().addNewPrivateMessageUser(nick, message, false,
                    true);
            getServer().getServerEventBus().post(new NewPrivateMessage(nick));
        } else if (Utils.isNotEmpty(message)) {
            getServer().getServerEventBus().postAndStoreEvent(new PrivateMessageEvent(user,
                    getServer().getUser(), message), user);
        }
    }

    public void sendActionToUser(final String nick, final String action) {
        if (Utils.isNotEmpty(action)) {
            post(new PrivateActionCall(nick, action));
        }

        final PrivateMessageUser user = getServer().getUserChannelInterface()
                .getPrivateMessageUser(nick);
        if (user == null) {
            getServer().getUserChannelInterface().addNewPrivateMessageUser(nick, action, true,
                    true);
            getServer().getServerEventBus().post(new NewPrivateMessage(nick));
        } else {
            getServer().getServerEventBus().post(new NewPrivateMessage(nick));
            if (Utils.isNotEmpty(action)) {
                getServer().getServerEventBus().postAndStoreEvent(new PrivateActionEvent(user,
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

    public void sendClosePrivateMessage(final PrivateMessageUser user) {
        getServer().getUserChannelInterface().removePrivateMessageUser(user);

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final ServerEvent event = new PrivateMessageClosedEvent(user);
            getServer().getServerEventBus().post(event);
        }
    }

    public void sendJoin(final String channelName) {
        post(new ChannelJoinCall(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new ChannelMessageCall(channelName, message));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final Channel channel = getServer().getUserChannelInterface().getChannel(
                    channelName);
            final ChannelEvent event = new MessageEvent(channel, message,
                    getServer().getUser().getPrettyNick(channel));
            getServer().getServerEventBus().postAndStoreEvent(event, channel);
        }
    }

    public void sendSlap(final String channelName, final String nick) {
        //final String message = InterfaceHolders.getEventResponses().getSlapMessage(nick);
        //sendActionToChannel(channelName, message);
    }

    public void sendActionToChannel(final String channelName, final String action) {
        post(new ChannelActionCall(channelName, action));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final Channel channel = getServer().getUserChannelInterface()
                    .getChannel(channelName);
            final ChannelEvent event = new ActionEvent(channel, action,
                    getServer().getUser().getPrettyNick(channel));
            getServer().getServerEventBus().postAndStoreEvent(event, channel);
        }
    }

    public void sendKick(String channelName, String nick, String reason) {
        post(new ChannelKickCall(channelName, nick, reason));
    }

    Server getServer() {
        return mServer;
    }
}