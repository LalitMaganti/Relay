package com.fusionx.relay.communication;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.User;
import com.fusionx.relay.event.ChannelEvent;
import com.fusionx.relay.event.ConnectedEvent;
import com.fusionx.relay.event.DisconnectEvent;
import com.fusionx.relay.event.JoinEvent;
import com.fusionx.relay.event.KickEvent;
import com.fusionx.relay.event.NickInUseEvent;
import com.fusionx.relay.event.PartEvent;
import com.fusionx.relay.event.PrivateActionEvent;
import com.fusionx.relay.event.PrivateEvent;
import com.fusionx.relay.event.PrivateMessageEvent;
import com.fusionx.relay.event.ServerEvent;
import com.fusionx.relay.event.SwitchToServerEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.os.Handler;
import android.os.Looper;

public class ServerEventBus extends Bus {

    private final Handler mMainThread = new Handler(Looper.getMainLooper());

    private boolean mDisplayed;

    public ServerEventBus() {
        super(ThreadEnforcer.ANY);
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mMainThread.post(new Runnable() {
                @Override
                public void run() {
                    ServerEventBus.super.post(event);
                }
            });
        }
    }

    /**
     * Start of sending messages
     */
    private void sendServerEvent(final Server server, final ServerEvent event) {
        if (server.getServerCache().isCached()) {
            post(event);
        } else {
            server.onServerEvent(event);
        }
    }

    private void sendChannelEvent(final Channel channel, final ChannelEvent event) {
        if (channel.isCached()) {
            post(event);
        } else {
            channel.onChannelEvent(event);
        }
    }

    private void sendUserEvent(final PrivateMessageUser user, final PrivateEvent event) {
        if (user.isCached()) {
            post(event);
        } else {
            user.onUserEvent(event);
        }
    }

    /*
     * End of internal methods
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
    // Generic events end

    public void onDisconnected(final Server server,
            final String disconnectLine, final boolean retryPending) {
        final DisconnectEvent event = new DisconnectEvent(disconnectLine, retryPending);
        sendServerEvent(server, event);
    }

    public JoinEvent onChannelJoined(final String channelName) {
        final JoinEvent event = new JoinEvent(channelName);
        post(event);
        return event;
    }

    public void onChannelParted(final String channelName) {
        final PartEvent event = new PartEvent(channelName);
        post(event);
    }

    public void onKicked(final String channelName) {
        final KickEvent event = new KickEvent(channelName);
        post(event);
    }

    public ChannelEvent onChannelMessage(final AppUser user, final Channel channel,
            final ChannelUser channelUser, final String rawMessage) {
        return onChannelMessage(user, channel, channelUser.getBracketedNick(channel),
                rawMessage);
    }

    public ChannelEvent onChannelMessage(final AppUser user, final Channel channel,
            final String nick, final String rawMessage) {
        String preMessage = InterfaceHolders.getEventResponses().getMessage(nick, rawMessage);
        if (rawMessage.toLowerCase().contains(user.getNick().toLowerCase())) {
            onUserMentioned(channel.getName());
            preMessage = "<bold>" + preMessage + "</bold>";
        }
        return sendGenericChannelEvent(channel, preMessage, false);
    }

    public ChannelEvent onChannelAction(final AppUser user, final Channel channel,
            final String nick, final String rawAction) {
        String finalMessage = InterfaceHolders.getEventResponses()
                .getActionMessage(nick, rawAction);
        if (rawAction.toLowerCase().contains(user.getNick().toLowerCase())) {
            onUserMentioned(channel.getName());
            finalMessage = "<bold>" + finalMessage + "</bold>";
        }
        return sendGenericChannelEvent(channel, finalMessage, false);
    }

    public ChannelEvent onChannelAction(final AppUser user, final Channel channel,
            final ChannelUser sendingUser, final String rawAction) {
        final String nick = sendingUser.getPrettyNick(channel);
        return onChannelAction(user, channel, nick, rawAction);
    }

    /**
     * Method used to send a private message. <p/> Method should not be used from anywhere but the
     * Server class.
     *
     * @param user       - the destination user object
     * @param sending    - the user who is sending the message - it may be us or it may be the other
     *                   user
     * @param rawMessage - the message being sent
     * @param newMessage - whether this conversation was open prior to this point
     */
    public PrivateEvent sendPrivateMessage(final PrivateMessageUser user, final User sending,
            final String rawMessage, final boolean newMessage) {
        final String message = InterfaceHolders.getEventResponses().getMessage(sending
                .getColorfulNick(), rawMessage);
        // TODO - change this to be specific for PMs
        onUserMentioned(user.getNick());
        final PrivateMessageEvent privateMessageEvent = new PrivateMessageEvent(user.getNick(),
                message, newMessage);
        sendUserEvent(user, privateMessageEvent);
        return privateMessageEvent;
    }

    public PrivateEvent sendPrivateAction(final PrivateMessageUser user, final User sendingUser,
            final String rawAction, final boolean newMessage) {
        final String message = InterfaceHolders.getEventResponses().getActionMessage(sendingUser
                .getColorfulNick(), rawAction);
        // TODO - change this to be specific for PMs
        if (sendingUser.equals(user)) {
            onUserMentioned(user.getNick());
        }
        final PrivateActionEvent privateMessageEvent = new PrivateActionEvent(user.getNick(),
                message, newMessage);
        sendUserEvent(user, privateMessageEvent);
        return privateMessageEvent;
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

    public void sendInviteEvent(Server server, String channelName) {
        // TODO figure out what to do here
    }

    // TODO - fix this
    void onUserMentioned(final String messageDestination) {
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

    // Getters and setters
    public void setDisplayed(final boolean toast) {
        mDisplayed = toast;
    }
}