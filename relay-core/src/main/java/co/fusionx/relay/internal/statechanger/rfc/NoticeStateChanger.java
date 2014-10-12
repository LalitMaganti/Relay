package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.event.channel.ChannelNoticeEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.event.server.NoticeEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.rfc.NoticeParser;
import co.fusionx.relay.util.ParseUtils;

public class NoticeStateChanger implements NoticeParser.NoticeObserver {

    private final InternalServer mServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryManager;

    public NoticeStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
        mQueryManager = queryManager;
    }

    @Override
    public void onNotice(final String prefix, final String recipient, final String notice) {
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);

        if (ChannelPrefix.isPrefix(recipient.charAt(0))) {
            onParseChannelNotice(recipient, notice, sendingNick);
        } else if (recipient.equals(mUserChannelGroup.getUser().getNick().getNickAsString())) {
            onParseUserNotice(sendingNick, notice);
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