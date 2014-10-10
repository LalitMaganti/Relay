package co.fusionx.relay.internal.statechanger;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.event.channel.ChannelNoticeEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.event.server.NoticeEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.parser.CTCPParser;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.ParseUtils;

public class NoticeStateChanger implements CommandParser {

    private final InternalServer mServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryManager;

    private final CTCPParser mCTCPParser;

    public NoticeStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager, final CTCPParser ctcpParser) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
        mQueryManager = queryManager;
        mCTCPParser = ctcpParser;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String notice = parsedArray.get(1);

        // Notices can be CTCP replies
        if (CTCPParser.isCtcp(notice)) {
            mCTCPParser.onParseReply(parsedArray, prefix);
        } else {
            final String sendingNick = ParseUtils.getNickFromPrefix(prefix);
            final String recipient = parsedArray.get(0);

            if (ChannelPrefix.isPrefix(recipient.charAt(0))) {
                onParseChannelNotice(recipient, notice, sendingNick);
            } else if (recipient.equals(mUserChannelGroup.getUser().getNick().getNickAsString())) {
                onParseUserNotice(sendingNick, notice);
            }
        }
    }

    private void onParseChannelNotice(final String channelName, final String sendingNick,
            final String notice) {
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);
        if (optChannel.isPresent()) {
            final InternalChannel channel = optChannel.get();
            channel.postEvent(new ChannelNoticeEvent(channel, sendingNick, notice));
        } else {
            // If we're not in this channel then send the notice to the server instead
            // TODO - maybe figure out why this is happening
            mServer.postEvent(new NoticeEvent(mServer, sendingNick, notice));
        }
    }

    private void onParseUserNotice(final String sendingNick, final String notice) {
        final Optional<InternalQueryUser> optUser = mQueryManager.getQueryUser(sendingNick);
        if (optUser.isPresent()) {
            final InternalQueryUser user = optUser.get();
            user.postEvent(new QueryMessageWorldEvent(user, notice));
        } else {
            mServer.postEvent(new NoticeEvent(mServer, sendingNick, notice));
        }
    }
}