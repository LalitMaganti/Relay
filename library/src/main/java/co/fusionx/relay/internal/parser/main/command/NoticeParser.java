package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.event.channel.ChannelNoticeEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.event.server.NoticeEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.util.ParseUtils;

public class NoticeParser extends CommandParser {

    private final CTCPParser mCTCPParser;

    public NoticeParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface,
            final CTCPParser CTCPParser) {
        super(server, userChannelInterface);

        mCTCPParser = CTCPParser;
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String notice = parsedArray.get(1);

        // Notices can be CTCP replies
        if (CTCPParser.isCtcp(notice)) {
            mCTCPParser.onParseReply(parsedArray, prefix);
        } else {
            final String sendingNick = ParseUtils.getNickFromPrefix(prefix);
            final String recipient = parsedArray.get(0);

            if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelNotice(recipient, notice, sendingNick);
            } else if (recipient.equals(mUser.getNick().getNickAsString())) {
                onParseUserNotice(sendingNick, notice);
            }
        }
    }

    private void onParseChannelNotice(final String channelName, final String sendingNick,
            final String notice) {
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);
        if (optChannel.isPresent()) {
            final RelayChannel channel = optChannel.get();
            channel.postAndStoreEvent(new ChannelNoticeEvent(channel, sendingNick, notice));
        } else {
            // If we're not in this channel then send the notice to the server instead
            // TODO - maybe figure out why this is happening
            mServer.postAndStoreEvent(new NoticeEvent(mServer, sendingNick, notice));
        }
    }

    private void onParseUserNotice(final String sendingNick, final String notice) {
        final Optional<RelayQueryUser> optUser = mUser.getQueryUser(sendingNick);
        if (optUser.isPresent()) {
            final RelayQueryUser user = optUser.get();
            user.postAndStoreEvent(new QueryMessageWorldEvent(user, notice));
        } else {
            mServer.postAndStoreEvent(new NoticeEvent(mServer, sendingNick, notice));
        }
    }
}