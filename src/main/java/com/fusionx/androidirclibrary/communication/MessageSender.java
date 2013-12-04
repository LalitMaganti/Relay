/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary.communication;

import com.fusionx.androidirclibrary.Channel;
import com.fusionx.androidirclibrary.ChannelUser;
import com.fusionx.androidirclibrary.PrivateMessageUser;
import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.User;
import com.fusionx.androidirclibrary.event.ChannelEvent;
import com.fusionx.androidirclibrary.event.ConnectedEvent;
import com.fusionx.androidirclibrary.event.DisconnectEvent;
import com.fusionx.androidirclibrary.event.JoinEvent;
import com.fusionx.androidirclibrary.event.KickEvent;
import com.fusionx.androidirclibrary.event.NickInUseEvent;
import com.fusionx.androidirclibrary.event.PartEvent;
import com.fusionx.androidirclibrary.event.PrivateMessageEvent;
import com.fusionx.androidirclibrary.event.ServerEvent;
import com.fusionx.androidirclibrary.event.SwitchToServerEvent;
import com.fusionx.androidirclibrary.event.UserEvent;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.squareup.otto.Bus;

import java.util.HashMap;

public class MessageSender {

    private static final HashMap<String, MessageSender> mSenderMap = new HashMap<String,
            MessageSender>();

    private boolean mDisplayed;

    private ServerToFrontEndBus mBus;

    private String mServerName;

    private MessageSender() {
    }

    public static MessageSender getSender(final String serverName, final boolean nullable) {
        synchronized (mSenderMap) {
            MessageSender sender = mSenderMap.get(serverName);
            if (sender == null && !nullable) {
                sender = new MessageSender();
                sender.mServerName = serverName;
                sender.mBus = new ServerToFrontEndBus(sender);
                mSenderMap.put(serverName, sender);
            }
            return sender;
        }
    }

    public static MessageSender getSender(final String serverName) {
        return getSender(serverName, false);
    }

    public static void clear() {
        synchronized (mSenderMap) {
            mSenderMap.clear();
        }
    }

    public Bus getBus() {
        return mBus;
    }

    void removeSender() {
        synchronized (mSenderMap) {
            mSenderMap.remove(mServerName);
        }
    }

    public void setDisplayed(final boolean toast) {
        mDisplayed = toast;
    }

    /**
     * Start of sending messages
     */
    private void sendServerEvent(final Server server, final ServerEvent event) {
        if (server.isCached()) {
            mBus.post(event);
        } else {
            server.onServerEvent(event);
        }
    }

    private void sendChannelEvent(final Channel channel, final ChannelEvent event) {
        if (channel.isCached()) {
            mBus.post(event);
        } else {
            channel.onChannelEvent(event);
        }
    }

    private void sendUserEvent(final PrivateMessageUser user, final UserEvent event) {
        if (user.isCached()) {
            mBus.post(event);
        } else {
            user.onUserEvent(event);
        }
    }

    /*
    End of internal methods
     */

    // Generic events start
    public ServerEvent sendGenericServerEvent(final Server server, final String message) {
        final ServerEvent event = new ServerEvent(message);
        sendServerEvent(server, event);
        return event;
    }

    public ChannelEvent sendGenericChannelEvent(final Channel channel, final String message,
            final boolean userListChanged) {
        final ChannelEvent event = new ChannelEvent(channel.getName(), message,
                userListChanged);
        sendChannelEvent(channel, event);
        return event;
    }

    private UserEvent sendGenericUserEvent(final PrivateMessageUser user, final String message) {
        final UserEvent privateMessageEvent = new UserEvent(user.getNick(), message);
        sendUserEvent(user, privateMessageEvent);
        return privateMessageEvent;
    }

    // Generic events end
    public DisconnectEvent sendDisconnect(final Server server,
            final String disconnectLine, final boolean retryPending) {
        final DisconnectEvent event = new DisconnectEvent(disconnectLine, retryPending);
        sendServerEvent(server, event);
        return event;
    }

    public PrivateMessageEvent sendNewPrivateMessage(final String nick) {
        final PrivateMessageEvent event = new PrivateMessageEvent(nick);
        mBus.post(event);
        return event;
    }

    public JoinEvent sendChanelJoined(final String channelName) {
        final JoinEvent event = new JoinEvent(channelName);
        mBus.post(event);
        return event;
    }

    public PartEvent sendChanelParted(final String channelName) {
        final PartEvent event = new PartEvent(channelName);
        mBus.post(event);
        return event;
    }

    public KickEvent sendKicked(final String channelName) {
        final KickEvent event = new KickEvent(channelName);
        mBus.post(event);
        return event;
    }

    public UserEvent sendPrivateAction(final PrivateMessageUser user, final User sendingUser,
            final String rawAction) {
        final String message = InterfaceHolders.getEventResponses().getActionMessage(sendingUser
                .getColorfulNick(), rawAction);
        // TODO - change this to be specific for PMs
        if (sendingUser.equals(user)) {
            mention(user.getNick());
        }
        return sendGenericUserEvent(user, message);
    }

    public ChannelEvent sendChannelAction(final String userNick,
            final Channel channel, final ChannelUser sendingUser,
            final String rawAction) {
        String finalMessage = InterfaceHolders.getEventResponses().getActionMessage(sendingUser
                .getPrettyNick(channel), rawAction);
        if (rawAction.toLowerCase().contains(userNick.toLowerCase())) {
            mention(channel.getName());
            finalMessage = "<b>" + finalMessage + "</b>";
        }
        return sendGenericChannelEvent(channel, finalMessage, false);
    }

    /**
     * Method used to send a private message. <p/> Method should not be used from anywhere but the
     * Server class.
     *
     * @param user       - the destination user object
     * @param sending    - the user who is sending the message - it may be us or it may be the other
     *                   user
     * @param rawMessage - the message being sent
     */
    public UserEvent sendPrivateMessage(final PrivateMessageUser user, final User sending,
            final String rawMessage) {
        final String message = ""; //String.format(mContext.getString(R.string.parser_message),
        //sending.getColorfulNick(), rawMessage);
        // TODO - change this to be specific for PMs
        mention(user.getNick());
        return sendGenericUserEvent(user, message);
    }

    public ChannelEvent sendMessageToChannel(final String userNick, final Channel channel,
            final String sendingNick, final String rawMessage) {
        String preMessage = InterfaceHolders.getEventResponses().getMessage(sendingNick,
                rawMessage);
        if (rawMessage.toLowerCase().contains(userNick.toLowerCase())) {
            mention(channel.getName());
            preMessage = "<b>" + preMessage + "</b>";
        }
        return sendGenericChannelEvent(channel, preMessage, false);
    }

    public NickInUseEvent sendNickInUseMessage(final Server server) {
        final NickInUseEvent event = new NickInUseEvent();
        sendServerEvent(server, event);
        return event;
    }

    public SwitchToServerEvent sendSwitchToServerEvent(final Server server, final String message) {
        final SwitchToServerEvent event = new SwitchToServerEvent(message);
        sendServerEvent(server, event);
        return event;
    }

    public void sendConnected(final Server server, final String url) {
        final ConnectedEvent event = new ConnectedEvent(url);
        sendServerEvent(server, event);
    }

    void mention(final String messageDestination) {
    /*    if (mDisplayed) {
            mBus.post(new MentionEvent(messageDestination));
        } else {
            final NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            final Intent intent = new Intent(mContext, UIUtils.getIRCActivity(mContext));
            intent.putExtra("serverTitle", mServerName);
            intent.putExtra("mention", messageDestination);
            final TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mContext);
            taskStackBuilder.addParentStack(UIUtils.getIRCActivity(mContext));
            taskStackBuilder.addNextIntent(intent);
            final PendingIntent pIntent = taskStackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            final Notification notification = new NotificationCompat.Builder(mContext)
                    .setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(mContext.getString(R.string.service_you_mentioned) + " " +
                            messageDestination)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setTicker(mContext.getString(R.string.service_you_mentioned) + " " +
                            messageDestination)
                    .setContentIntent(pIntent).build();
            mNotificationManager.notify(345, notification);
        }*/
    }

    public void sendInviteEvent(Server server, String channelName) {
        // TODO figure out what to do here
    }
}