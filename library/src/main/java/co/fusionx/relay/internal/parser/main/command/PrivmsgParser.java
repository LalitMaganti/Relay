package co.fusionx.relay.internal.parser.main.command;

import android.text.TextUtils;
import android.util.Pair;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.internal.parser.main.MentionParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.util.Utils;

public class PrivmsgParser extends CommandParser {

    private final CTCPParser mCTCPParser;

    public PrivmsgParser(final RelayServer server, final CTCPParser CTCPParser) {
        super(server);

        mCTCPParser = CTCPParser;
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.get(0);
        final String message = parsedArray.get(1);

        // PRIVMSGs can be CTCP commands
        if (CTCPParser.isCtcp(message)) {
            mCTCPParser.onParseCommand(prefix, recipient, message);
        } else {
            final String nick = ParseUtils.getNickFromPrefix(prefix);
            final Pair<String, List<FormatSpanInfo>> messageAndColors =
                    Utils.parseAndStripColorsFromMessage(message);
            if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelMessage(nick, recipient,
                        messageAndColors.first, messageAndColors.second);
            } else {
                onParsePrivateMessage(nick, messageAndColors.first, messageAndColors.second);
            }
        }
    }

    private void onParsePrivateMessage(final String nick, final String message,
            final List<FormatSpanInfo> formats) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUserChannelInterface.addQueryUser(nick));
        if (!optional.isPresent()) {
            mServer.postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
        user.postAndStoreEvent(new QueryMessageWorldEvent(user, message, formats));
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String message, final List<FormatSpanInfo> formats) {
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            final String ownNick = mServer.getUser().getNick().getNickAsString();
            final boolean mention = !TextUtils.equals(sendingNick, ownNick)
                    ? MentionParser.onMentionableCommand(message, ownNick) : false;

            final Optional<RelayChannelUser> optUser = mUserChannelInterface.getUser(sendingNick);
            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldMessageEvent(channel, message,
                        optUser.get(), mention, formats);
            } else {
                event = new ChannelWorldMessageEvent(channel, message,
                        sendingNick, mention, formats);
            }
            channel.postAndStoreEvent(event);
        });
    }
}