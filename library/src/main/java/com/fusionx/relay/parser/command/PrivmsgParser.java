package com.fusionx.relay.parser.command;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import com.fusionx.relay.event.query.QueryMessageWorldEvent;
import com.fusionx.relay.event.server.NewPrivateMessageEvent;
import com.fusionx.relay.parser.MentionParser;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.Utils;

import java.util.List;

public class PrivmsgParser extends CommandParser {

    private final CtcpParser mCtcpParser;

    public PrivmsgParser(final RelayServer server, final CtcpParser ctcpParser) {
        super(server);

        mCtcpParser = ctcpParser;
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String message = parsedArray.get(3);

        // PRIVMSGs can be CTCP commands
        if (CtcpParser.isCtcp(message)) {
            mCtcpParser.onParseCommand(parsedArray, rawSource);
        } else {
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            if (getUserChannelInterface().shouldIgnoreUser(nick)) {
                return;
            }
            final String recipient = parsedArray.get(2);
            if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelMessage(nick, recipient, message);
            } else {
                onParsePrivateMessage(nick, message);
            }
        }
    }

    private void onParsePrivateMessage(final String nick, final String message) {
        final RelayQueryUser user = getUserChannelInterface().getQueryUser(nick);
        if (user == null) {
            getUserChannelInterface().addQueryUser(nick, message, false, false);
            getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(nick));
        } else {
            getServerEventBus().postAndStoreEvent(new QueryMessageWorldEvent(user, message), user);
        }
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String rawMessage) {
        final ChannelUser sendingUser = getUserChannelInterface().getUser(sendingNick);
        final RelayChannel channel = getUserChannelInterface().getChannel(channelName);
        // TODO - actually parse the colours
        final String message = Utils.stripColorsFromMessage(rawMessage);
        final boolean mention = MentionParser.onMentionableCommand(message,
                getServer().getUser().getNick().getNickAsString());

        final ChannelEvent event;
        if (sendingUser == null) {
            event = new ChannelWorldMessageEvent(channel, message, sendingNick, mention);
        } else {
            event = new ChannelWorldMessageEvent(channel, message, sendingUser, mention);
        }
        getServerEventBus().postAndStoreEvent(event, channel);
    }
}