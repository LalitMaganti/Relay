package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class NoticeParser extends CommandParser {

    private CtcpParser mCtcpParser;

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
            final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
            final String recipient = parsedArray.get(2);
            final String notice = parsedArray.get(3);

            final String formattedNotice = mEventResponses.getNoticeMessage(sendingUser, notice);

            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelNotice(recipient, formattedNotice);
            } else if (recipient.equals(mServer.getUser().getNick())) {
                onParseUserNotice(sendingUser, notice, formattedNotice);
            }
        }
    }

    public void onParseChannelNotice(final String channelName, final String formattedNotice) {
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        mServerEventBus.sendGenericChannelEvent(channel, formattedNotice, UserListChangeType.NONE);
    }

    public void onParseUserNotice(final String sendingNick, final String notice,
            final String formattedNotice) {
        final PrivateMessageUser user = mServer.getPrivateMessageUserIfExists(sendingNick);
        if (user != null) {
            mServer.onPrivateMessage(user, notice, false);
        } else {
            mServerEventBus.sendSwitchToServerEvent(mServer, formattedNotice);
        }
    }
}