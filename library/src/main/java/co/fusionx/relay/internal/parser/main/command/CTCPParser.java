package co.fusionx.relay.internal.parser.main.command;

import android.text.TextUtils;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldActionEvent;
import co.fusionx.relay.event.query.QueryActionWorldEvent;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.event.server.VersionEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelInterface;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.internal.parser.main.MentionParser;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayCtcpResponseSender;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class CTCPParser {

    private final RelayServer mServer;

    private final DCCParser mDCCParser;

    private final RelayUserChannelInterface mUserChannelInterface;

    private final RelayCtcpResponseSender mCtcpResponseSender;

    public CTCPParser(final RelayServer server, final BaseSender sender,
            final DCCParser dccParser) {
        mServer = server;
        mDCCParser = dccParser;

        mUserChannelInterface = server.getUserChannelInterface();

        mCtcpResponseSender = new RelayCtcpResponseSender(sender);
    }

    public static boolean isCtcp(final String message) {
        return message.startsWith("\u0001") && message.endsWith("\u0001");
    }

    // Commands start here
    public void onParseCommand(final String prefix,
            final String recipient, final String rawMessage) {
        final String message = rawMessage.substring(1, rawMessage.length() - 1);
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            onAction(recipient, sendingNick, message);
        } else if (message.startsWith("FINGER")) {
            mCtcpResponseSender.sendFingerResponse(sendingNick,
                    mServer.getConfiguration().getRealName());
        } else if (message.startsWith("VERSION")) {
            mCtcpResponseSender.sendVersionResponse(sendingNick);
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
            final String query = message.replace("ERRMSG ", "");
            mCtcpResponseSender.sendErrMsgResponse(sendingNick, query);
        } else if (message.startsWith("PING")) {
            final String timestamp = message.replace("PING ", "");
            mCtcpResponseSender.sendPingResponse(sendingNick, timestamp);
        } else if (message.startsWith("TIME")) {
            mCtcpResponseSender.sendTimeResponse(sendingNick);
        } else if (message.startsWith("DCC")) {
            final List<String> parsedDcc = ParseUtils.splitRawLineWithQuote(message);
            mDCCParser.onParseCommand(parsedDcc, prefix);
        }
    }

    private void onAction(final String recipient, final String sendingNick, final String message) {
        final String action = message.replace("ACTION ", "");
        if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
            onParseChannelAction(recipient, sendingNick, action);
        } else {
            onParseUserAction(recipient, action);
        }
    }

    private void onParseUserAction(final String nick, final String action) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUserChannelInterface.addQueryUser(nick));
        if (!optional.isPresent()) {
            mServer.postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
        user.postAndStoreEvent(new QueryActionWorldEvent(user, action));
    }

    private void onParseChannelAction(final String channelName, final String sendingNick,
            final String action) {
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            final Optional<RelayChannelUser> optUser = mUserChannelInterface.getUser(sendingNick);
            final String ownNick = mServer.getUser().getNick().getNickAsString();
            final boolean mention = !TextUtils.equals(sendingNick, ownNick)
                    ? MentionParser.onMentionableCommand(action, ownNick) : false;

            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldActionEvent(channel, action, optUser.get(), mention);
            } else {
                event = new ChannelWorldActionEvent(channel, action, sendingNick, mention);
            }
            channel.postAndStoreEvent(event);
        });
    }
    // Commands End Here

    // Replies start here
    public void onParseReply(final List<String> parsedArray, final String prefix) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            // Nothing should be done for an action reply - it is technically invalid
        } else if (message.startsWith("FINGER")) {
        } else if (message.startsWith("VERSION")) {
            final String nick = ParseUtils.getNickFromPrefix(prefix);
            final String version = message.replace("VERSION", "");
            mServer.postAndStoreEvent(new VersionEvent(mServer, nick, version));
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
        } else if (message.startsWith("PING")) {
        } else if (message.startsWith("TIME")) {
        }
    }
    // Replies end here
}