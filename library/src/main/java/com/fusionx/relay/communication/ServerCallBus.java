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
import com.fusionx.relay.connection.ServerConnection;
import com.fusionx.relay.event.SwitchToPrivateMessage;
import com.fusionx.relay.event.channel.ActionEvent;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.MessageEvent;
import com.fusionx.relay.event.server.DisconnectEvent;
import com.fusionx.relay.event.user.PrivateActionEvent;
import com.fusionx.relay.event.user.PrivateMessageEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.apache.commons.lang3.StringUtils;

public class ServerCallBus extends Bus {

    private final ServerConnection mConnection;

    public ServerCallBus(final ServerConnection connection) {
        super(ThreadEnforcer.ANY);

        mConnection = connection;
    }

    @Override
    public void post(final Object event) {
        mConnection.getServerCallHandler().post(new Runnable() {
            @Override
            public void run() {
                ServerCallBus.super.post(event);
            }
        });
    }

    public void sendMode(final String channelName, final String destination, final String mode) {
        post(new ModeCall(channelName, destination, mode));
    }

    public void sendUnknownEvent(final String event) {
        //getServer().getServerEventBus().sendSwitchToServerEvent(event);
    }

    public void sendUserWhois(final String nick) {
        //post(new WhoisEvent(nick));
    }

    public void sendRawLine(final String rawLine) {
        post(new RawCall(rawLine));
    }

    public void sendDisconnect() {
        getServer().getServerEventBus().post(new DisconnectEvent("", true, false));
        mConnection.onDisconnect();
    }

    public void sendMessageToUser(final String nick, final String message) {
        if (StringUtils.isNotEmpty(message)) {
            post(new PrivateMessageCall(nick, message));
        }

        final PrivateMessageUser user = getServer().getUserChannelInterface()
                .getPrivateMessageUserIfExists(nick);
        if (user == null) {
            getServer().getUserChannelInterface().getNewPrivateMessageUser(nick, message, false);
            getServer().getServerEventBus().post(new SwitchToPrivateMessage(nick));
        } else {
            getServer().getServerEventBus().post(new SwitchToPrivateMessage(nick));
            if (StringUtils.isNotEmpty(message)) {
                getServer().getServerEventBus().postAndStoreEvent(new PrivateMessageEvent(user,
                        getServer().getUser(), message), user);
            }
        }
    }

    public void sendActionToUser(final String nick, final String action) {
        if (StringUtils.isNotEmpty(action)) {
            post(new PrivateActionCall(nick, action));
        }

        final PrivateMessageUser user = getServer().getUserChannelInterface()
                .getPrivateMessageUserIfExists(nick);
        if (user == null) {
            getServer().getUserChannelInterface().getNewPrivateMessageUser(nick, action, true);
            getServer().getServerEventBus().post(new SwitchToPrivateMessage(nick));
        } else {
            getServer().getServerEventBus().post(new SwitchToPrivateMessage(nick));
            if (StringUtils.isNotEmpty(action)) {
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
    }

    public void sendJoin(final String channelName) {
        post(new ChannelJoinCall(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new ChannelMessageCall(channelName, message));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final Channel channel = getServer().getUserChannelInterface().getChannel(channelName);
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
            final Channel channel = getServer().getUserChannelInterface().getChannel(channelName);
            final ChannelEvent event = new ActionEvent(channel, action,
                    getServer().getUser().getPrettyNick(channel));
            getServer().getServerEventBus().postAndStoreEvent(event, channel);
        }
    }

    Server getServer() {
        return mConnection.getServer();
    }

    public void sendKick(String channelName, String nick, String reason) {
        post(new ChannelKickCall(channelName, nick, reason));
    }
}