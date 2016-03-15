package co.fusionx.relay.internal.parser.main.command;

import android.text.TextUtils;

import com.google.common.base.Optional;

import java.util.List;

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
            if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelMessage(nick, recipient, message);
            } else {
                onParsePrivateMessage(nick, message);
            }
        }
    }

    private void onParsePrivateMessage(final String nick, final String message) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUserChannelInterface.addQueryUser(nick));
        if (!optional.isPresent()) {
            mServer.postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
        user.postAndStoreEvent(new QueryMessageWorldEvent(user, message));
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String rawMessage) {
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            // TODO - actually parse the colours
            final String message = Utils.stripColorsFromMessage(rawMessage);
            final String ownNick = mServer.getUser().getNick().getNickAsString();
            final boolean mention = !TextUtils.equals(sendingNick, ownNick)
                    ? MentionParser.onMentionableCommand(message, ownNick) : false;

            final Optional<RelayChannelUser> optUser = mUserChannelInterface.getUser(sendingNick);
            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldMessageEvent(channel, message, optUser.get(), mention);
            } else {
                event = new ChannelWorldMessageEvent(channel, message, sendingNick, mention);
            }
            channel.postAndStoreEvent(event);
        });
    }
}