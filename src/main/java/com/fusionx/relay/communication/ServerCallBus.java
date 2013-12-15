package com.fusionx.relay.communication;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.connection.ServerConnection;
import com.fusionx.relay.event.ActionEvent;
import com.fusionx.relay.event.DisconnectEvent;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.JoinEvent;
import com.fusionx.relay.event.MessageEvent;
import com.fusionx.relay.event.ModeEvent;
import com.fusionx.relay.event.NickChangeEvent;
import com.fusionx.relay.event.PartEvent;
import com.fusionx.relay.event.PrivateActionEvent;
import com.fusionx.relay.event.PrivateMessageEvent;
import com.fusionx.relay.event.WhoisEvent;
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
        post(new ModeEvent(channelName, destination, mode));
    }

    public void sendUnknownEvent(final String event) {
        getServer().getServerEventBus().sendSwitchToServerEvent(getServer(), event);
    }

    public void sendUserWhois(final String nick) {
        post(new WhoisEvent(nick));
    }

    public void sendRawLine(final String rawLine) {
        post(new Event(rawLine));
    }

    public void sendDisconnect() {
        getServer().getServerEventBus().post(new DisconnectEvent("", false, true));
        mConnection.onDisconnect();
    }

    public void sendMessageToUser(final String userNick, final String message) {
        final PrivateMessageUser user = getServer().getPrivateMessageUser(userNick, message);
        if (StringUtils.isNotEmpty(message)) {
            final boolean isPrivateMessageOpen = getServer().getUser().isPrivateMessageOpen(user);
            post(new PrivateMessageEvent(userNick, message, !isPrivateMessageOpen));
        }

        getServer().onPrivateMessage(user, message, true);
    }

    public void sendActionToUser(final String userNick, final String action) {
        final PrivateMessageUser user = getServer().getPrivateMessageUser(userNick, action);
        if (StringUtils.isNotEmpty(action)) {
            final boolean isPrivateMessageOpen = getServer().getUser().isPrivateMessageOpen(user);
            post(new PrivateActionEvent(userNick, action, !isPrivateMessageOpen));
        }

        getServer().onPrivateAction(user, action, true);
    }

    public void sendNickChange(final String newNick) {
        post(new NickChangeEvent(getServer().getUser().getNick(), newNick));
    }

    public void sendPart(final String channelName) {
        post(new PartEvent(channelName, InterfaceHolders.getPreferences().getPartReason()));
    }

    public void sendClosePrivateMessage(final String nick) {
        sendClosePrivateMessage(getServer().getPrivateMessageUserIfExists(nick));
    }

    public void sendClosePrivateMessage(final PrivateMessageUser user) {
        getServer().getUser().closePrivateMessage(user);
    }

    public void sendJoin(final String channelName) {
        post(new JoinEvent(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new MessageEvent(channelName, message));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final Channel channel = getServer().getUserChannelInterface().getChannel(channelName);
            final AppUser user = getServer().getUser();
            getServer().getServerEventBus().onChannelMessage(user, channel, user, message);
        }
    }

    public void sendActionToChannel(final String channelName, final String action) {
        post(new ActionEvent(channelName, action));

        if (InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            final Channel channel = getServer().getUserChannelInterface().getChannel(channelName);
            final AppUser user = getServer().getUser();
            getServer().getServerEventBus().onChannelAction(user, channel, user, action);
        }
    }

    Server getServer() {
        return mConnection.getServer();
    }
}