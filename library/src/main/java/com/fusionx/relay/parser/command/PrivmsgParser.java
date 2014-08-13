package com.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import com.fusionx.relay.event.query.QueryMessageWorldEvent;
import com.fusionx.relay.event.server.NewPrivateMessageEvent;
import com.fusionx.relay.parser.MentionParser;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.LogUtils;
import com.fusionx.relay.util.Optionals;
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
            if (mUserChannelInterface.shouldIgnoreUser(nick)) {
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
        final Optional<RelayQueryUser> optUser = mUserChannelInterface.getQueryUser(nick);
        if (optUser.isPresent()) {
            final RelayQueryUser user = optUser.get();
            mServerEventBus.postAndStoreEvent(new QueryMessageWorldEvent(user, message), user);
        } else {
            final RelayQueryUser user = mUserChannelInterface
                    .addQueryUser(nick, message, false, false);
            mServerEventBus.postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String rawMessage) {
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel);
        Optionals.ifPresent(optChannel, channel -> {
            // TODO - actually parse the colours
            final String message = Utils.stripColorsFromMessage(rawMessage);
            final boolean mention = MentionParser.onMentionableCommand(message,
                    mServer.getUser().getNick().getNickAsString());

            final Optional<RelayChannelUser> optUser = mUserChannelInterface.getUser(sendingNick);
            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldMessageEvent(channel, message, optUser.get(), mention);
            } else {
                event = new ChannelWorldMessageEvent(channel, message, sendingNick, mention);
            }
            mServerEventBus.postAndStoreEvent(event, channel);
        });
    }
}