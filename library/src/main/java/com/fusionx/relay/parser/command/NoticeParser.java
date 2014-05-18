package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.QueryUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelNoticeEvent;
import com.fusionx.relay.event.server.PrivateNoticeEvent;
import com.fusionx.relay.event.query.QueryMessageWorldEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class NoticeParser extends CommandParser {

    private final CtcpParser mCtcpParser;

    public NoticeParser(Server server, final CtcpParser ctcpParser) {
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

            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelNotice(recipient, notice, sendingNick);
            } else if (recipient.equals(getServer().getUser().getNick().getNickAsString())) {
                onParseUserNotice(sendingNick, notice);
            }
        }
    }

    void onParseChannelNotice(final String channelName, final String sendingNick,
            final String notice) {
        final Channel channel = getUserChannelInterface().getChannel(channelName);
        final ChannelEvent event = new ChannelNoticeEvent(channel, sendingNick, notice);
        getServerEventBus().postAndStoreEvent(event, channel);
    }

    void onParseUserNotice(final String sendingNick, final String notice) {
        final QueryUser user = getUserChannelInterface()
                .getQueryUser(sendingNick);
        if (user == null) {
            getServerEventBus().postAndStoreEvent(new PrivateNoticeEvent(notice, sendingNick));
        } else {
            getServerEventBus().postAndStoreEvent(new QueryMessageWorldEvent(user, notice), user);
        }
    }
}