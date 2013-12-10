package com.fusionx.androidirclibrary.communication;

import com.fusionx.androidirclibrary.Channel;
import com.fusionx.androidirclibrary.PrivateMessageUser;
import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.connection.ServerConnection;
import com.fusionx.androidirclibrary.event.ActionEvent;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.event.JoinEvent;
import com.fusionx.androidirclibrary.event.MessageEvent;
import com.fusionx.androidirclibrary.event.ModeEvent;
import com.fusionx.androidirclibrary.event.NickChangeEvent;
import com.fusionx.androidirclibrary.event.PartEvent;
import com.fusionx.androidirclibrary.event.PrivateMessageEvent;
import com.fusionx.androidirclibrary.event.WhoisEvent;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class ServerReceiverBus extends Bus {

    private final ServerConnection mConnection;

    public ServerReceiverBus(final ServerConnection connection) {
        super(ThreadEnforcer.ANY);

        mConnection = connection;
    }

    @Override
    public void post(final Object event) {
        mConnection.getServerHandler().post(new Runnable() {
            @Override
            public void run() {
                ServerReceiverBus.super.post(event);
            }
        });
    }

    public void sendMode(final String channelName, final String destination, final String mode) {
        post(new ModeEvent(channelName, destination, mode));
    }

    public void sendUnknownEvent(final String event) {
        getServer().getServerSenderBus().sendSwitchToServerEvent(getServer(), event);
    }

    public void sendUserWhois(final String nick) {
        post(new WhoisEvent(nick));
    }

    public void sendRawLine(final String rawLine) {
        post(new Event(rawLine));
    }

    public void sendDisconnect() {
        mConnection.onDisconnect();
    }

    public void sendMessageToUser(final String userNick, final String message) {
        final PrivateMessageUser user = getServer().getPrivateMessageUser(userNick);
        final boolean isPrivateMessageOpen = getServer().getUser().isPrivateMessageOpen(user);
        post(new PrivateMessageEvent(message, userNick, !isPrivateMessageOpen));

        getServer().onPrivateMessage(user, message, true);
    }

    public void sendActionToUser(final String userNick, final String action) {
        final PrivateMessageUser user = getServer().getPrivateMessageUser(userNick);
        final boolean isPrivateMessageOpen = getServer().getUser().isPrivateMessageOpen(user);
        post(new PrivateMessageEvent(action, userNick,
                !isPrivateMessageOpen));

        getServer().onPrivateAction(user, action, true);
    }

    public void sendNickChange(final String newNick) {
        post(new NickChangeEvent(getServer().getUser().getNick(), newNick));
    }

    public void sendPart(final String channelName) {
        post(new PartEvent(channelName, InterfaceHolders.getPreferences().getPartReason()));
    }

    public void sendClosePrivateMessage(final String nick) {
        sendClosePrivateMessage(getServer().getPrivateMessageUser(nick));
    }

    public void sendClosePrivateMessage(final PrivateMessageUser user) {
        getServer().getUser().closePrivateMessage(user);
    }

    public void sendJoin(final String channelName) {
        post(new JoinEvent(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new MessageEvent(channelName, message));

        if (InterfaceHolders.getPreferences().shouldSendSelfMessageEvent()) {
            final Channel channel = getServer().getUserChannelInterface().getChannel(channelName);
            getServer().getServerSenderBus().sendMessageToChannel(getServer().getUser(), channel,
                    getServer().getUser(), message);
        }
    }

    public void sendActionToChannel(final String channelName, final String action) {
        post(new ActionEvent(channelName, action));

        if (InterfaceHolders.getPreferences().shouldSendSelfMessageEvent()) {
            final Channel channel = getServer().getUserChannelInterface().getChannel(channelName);
            getServer().getServerSenderBus().sendChannelAction(getServer().getUser(), channel,
                    getServer().getUser(), action);
        }
    }

    Server getServer() {
        return mConnection.getServer();
    }
}