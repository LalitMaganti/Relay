package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.util.Utils;

public class PrivmsgParser implements CommandParser {

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryManager;

    private final CTCPParser mCTCPParser;

    public PrivmsgParser(final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager, final CTCPParser ctcpParser) {
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;
        mQueryManager = queryManager;

        mCTCPParser = ctcpParser;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.get(0);
        final String message = parsedArray.get(1);

        // PRIVMSGs can be CTCP commands
        if (CTCPParser.isCtcp(message)) {
            mCTCPParser.onParseCommand(prefix, recipient, message);
        } else {
            final String nick = ParseUtils.getNickFromPrefix(prefix);
            if (ChannelPrefix.isPrefix(recipient.charAt(0))) {
                onParseChannelMessage(nick, recipient, message);
            } else {
                onParsePrivateMessage(nick, message);
            }
        }
    }

    private void onParsePrivateMessage(final String nick, final String message) {
        final InternalQueryUser user = mQueryManager.getOrAddQueryUser(nick);
        user.postEvent(new QueryMessageWorldEvent(user, message));
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String rawMessage) {
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);

        Optionals.run(optChannel, channel -> {
            // TODO - actually parse the colours
            final String message = Utils.stripColorsFromMessage(rawMessage);
            final boolean mention = MentionParser.onMentionableCommand(message,
                    mUserChannelGroup.getUser().getNick().getNickAsString());

            final Optional<InternalChannelUser> optUser = mUserChannelGroup.getUser(sendingNick);
            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldMessageEvent(channel, message, optUser.get(), mention);
            } else {
                event = new ChannelWorldMessageEvent(channel, message, sendingNick, mention);
            }
            channel.postEvent(event);
        }, () -> LogUtils.logOptionalBug(mInternalServer.getConfiguration()));
    }
}