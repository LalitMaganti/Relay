package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.QueryUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.call.ERRMSGResponseCall;
import com.fusionx.relay.call.VersionResponseCall;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelWorldActionEvent;
import com.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import com.fusionx.relay.event.query.QueryActionWorldEvent;
import com.fusionx.relay.event.server.NewPrivateMessage;
import com.fusionx.relay.event.server.VersionEvent;
import com.fusionx.relay.parser.MentionParser;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class CtcpParser {

    private final Server mServer;

    private final ServerEventBus mEventBus;

    public CtcpParser(Server server) {
        mServer = server;
        mEventBus = server.getServerEventBus();
    }

    public static boolean isCtcp(final String message) {
        return message.startsWith("\u0001") && message.endsWith("\u0001");
    }

    // Commands start here
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        final String nick = IRCUtils.getNickFromRaw(rawSource);
        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            onAction(parsedArray, rawSource);
        } else if (message.startsWith("FINGER")) {
            getServer().getServerCallBus().post(new FingerResponseCall(nick, mServer));
        } else if (message.startsWith("VERSION")) {
            getServer().getServerCallBus().post(new VersionResponseCall(nick));
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
            final String query = message.replace("ERRMSG ", "");
            getServer().getServerCallBus().post(new ERRMSGResponseCall(nick, query));
        } else if (message.startsWith("PING")) {
            final String timestamp = message.replace("PING ", "");
            getServer().getServerCallBus().post(new PingResponseCall(nick, timestamp));
        } else if (message.startsWith("TIME")) {
            getServer().getServerCallBus().post(new TimeResponseCall(nick));
        }
    }

    private void onAction(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        if (!getUserChannelInterface().shouldIgnoreUser(nick)) {
            final String action = parsedArray.get(3).replace("ACTION ", "");
            final String recipient = parsedArray.get(2);
            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelAction(recipient, nick, action);
            } else {
                onParseUserAction(nick, action);
            }
        }
    }

    private void onParseUserAction(final String nick, final String action) {
        final QueryUser user = getUserChannelInterface().getQueryUser(nick);
        if (user == null) {
            getUserChannelInterface().addNewPrivateMessageUser(nick, action, true, false);
            getServerEventBus().postAndStoreEvent(new NewPrivateMessage(nick));
        } else {
            getServerEventBus().postAndStoreEvent(new QueryActionWorldEvent(user, action), user);
        }
    }

    private void onParseChannelAction(final String channelName, final String sendingNick,
            final String action) {
        final Channel channel = getUserChannelInterface().getChannel(channelName);
        final WorldUser sendingUser = getUserChannelInterface().getUserIfExists(sendingNick);
        final boolean mention = MentionParser.onMentionableCommand(action,
                getServer().getUser().getNick().getNickAsString());
        final ChannelEvent event;
        if (sendingUser == null) {
            event = new ChannelWorldMessageEvent(channel, action, sendingNick, mention);
        } else {
            event = new ChannelWorldActionEvent(channel, action, sendingUser, mention);
        }
        getServerEventBus().postAndStoreEvent(event, channel);
    }
    // Commands End Here

    // Replies start here
    public void onParseReply(final List<String> parsedArray, final String rawSource) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            // Nothing should be done for an action reply - it is technically invalid
        } else if (message.startsWith("FINGER")) {
        } else if (message.startsWith("VERSION")) {
            // Pass this on to the server
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            final String version = message.replace("VERSION", "");
            getServerEventBus().postAndStoreEvent(new VersionEvent(nick, version));
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
        } else if (message.startsWith("PING")) {
        } else if (message.startsWith("TIME")) {
        }
    }
    // Replies end here

    private ServerEventBus getServerEventBus() {
        return mEventBus;
    }

    private Server getServer() {
        return mServer;
    }

    private UserChannelInterface getUserChannelInterface() {
        return mServer.getUserChannelInterface();
    }
}