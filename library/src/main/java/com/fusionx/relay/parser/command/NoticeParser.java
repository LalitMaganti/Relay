package com.fusionx.relay.parser.command;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelNoticeEvent;
import com.fusionx.relay.event.query.QueryMessageWorldEvent;
import com.fusionx.relay.event.server.NoticeEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class NoticeParser extends CommandParser {

    private final CtcpParser mCtcpParser;

    public NoticeParser(final RelayServer server, final CtcpParser ctcpParser) {
        super(server);

        mCtcpParser = ctcpParser;
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String message = parsedArray.get(3);

        // Notices can be CTCP replies
        if (CtcpParser.isCtcp(message)) {
            mCtcpParser.onParseReply(parsedArray, rawSource);
        } else {
            final String sendingNick = IRCUtils.getNickFromRaw(rawSource);
            final String recipient = parsedArray.get(2);
            final String notice = parsedArray.get(3);

            if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelNotice(recipient, notice, sendingNick);
            } else if (recipient.equals(getServer().getUser().getNick().getNickAsString())) {
                onParseUserNotice(sendingNick, notice);
            }
        }
    }

    private void onParseChannelNotice(final String channelName, final String sendingNick,
            final String notice) {
        final RelayChannel channel = getUserChannelInterface().getChannel(channelName);
        if (channel == null) {
            // If we're not in this channel then send the notice to the server instead
            // TODO - maybe figure out why this is happening
            getServerEventBus().postAndStoreEvent(new NoticeEvent(notice, sendingNick));
            return;
        }
        final ChannelEvent event = new ChannelNoticeEvent(channel, sendingNick, notice);
        getServerEventBus().postAndStoreEvent(event, channel);
    }

    private void onParseUserNotice(final String sendingNick, final String notice) {
        final RelayQueryUser user = getUserChannelInterface().getQueryUser(sendingNick);
        if (user == null) {
            getServerEventBus().postAndStoreEvent(new NoticeEvent(notice, sendingNick));
        } else {
            getServerEventBus().postAndStoreEvent(new QueryMessageWorldEvent(user, notice), user);
        }
    }
}