package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.internal.parser.main.MentionParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.util.Utils;

public class PrivmsgParser extends CommandParser {

    private final CTCPParser mCTCPParser;

    public PrivmsgParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface,
            final CTCPParser CTCPParser) {
        super(server, userChannelInterface);

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
        final Optional<RelayQueryUser> optional = mUser.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUser.addQueryUser(nick));
        if (!optional.isPresent()) {
            mServer.getBus().post(new NewPrivateMessageEvent(mServer, user));
        }
        user.getBus().post(new QueryMessageWorldEvent(user, message));
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String rawMessage) {
        final Optional<RelayChannel> optChannel = mDao.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            // TODO - actually parse the colours
            final String message = Utils.stripColorsFromMessage(rawMessage);
            final boolean mention = MentionParser.onMentionableCommand(message,
                    mUser.getNick().getNickAsString());

            final Optional<RelayChannelUser> optUser = mDao.getUser(sendingNick);
            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldMessageEvent(channel, message, optUser.get(), mention);
            } else {
                event = new ChannelWorldMessageEvent(channel, message, sendingNick, mention);
            }
            channel.getBus().post(event);
        });
    }
}