package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelNoticeEvent;
import com.fusionx.relay.event.server.PrivateNoticeEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;
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

        // Notices can be CTCP commands
        if (CtcpParser.isCtcpCommand(message)) {
            mCtcpParser.onParseCommand(parsedArray, rawSource);
        } else {
            final String sendingNick = IRCUtils.getNickFromRaw(rawSource);
            final String recipient = parsedArray.get(2);
            final String notice = parsedArray.get(3);

            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelNotice(recipient, notice, sendingNick);
            } else if (recipient.equals(mServer.getUser().getNick())) {
                onParseUserNotice(sendingNick, notice);
            }
        }
    }

    void onParseChannelNotice(final String channelName, final String sendingNick,
                              final String notice) {
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        final ChannelEvent event = new ChannelNoticeEvent(channel, sendingNick, notice);
        mServerEventBus.postAndStoreEvent(event, channel);
    }

    void onParseUserNotice(final String sendingNick, final String notice) {
        final PrivateMessageUser user = mUserChannelInterface
                .getPrivateMessageUser(sendingNick);
        if (user == null) {
            mServerEventBus.postAndStoreEvent(new PrivateNoticeEvent(notice, sendingNick));
        } else {
            mServerEventBus.postAndStoreEvent(new WorldPrivateMessageEvent(user, notice), user);
        }
    }
}