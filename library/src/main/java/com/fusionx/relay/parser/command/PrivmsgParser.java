package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class PrivmsgParser extends CommandParser {

    private CtcpParser mCtcpParser;

    public PrivmsgParser(final Server server, final CtcpParser ctcpParser) {
        super(server);

        mCtcpParser = ctcpParser;
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String message = parsedArray.get(3);

        // PRIVMSGs can be CTCP commands - e.g. actions to a channel or in a PM etc.
        if (CtcpParser.isCtcpCommand(message)) {
            mCtcpParser.onParseCommand(parsedArray, rawSource);
        } else {
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            if (!InterfaceHolders.getPreferences().shouldIgnoreUser(nick)) {
                final String recipient = parsedArray.get(2);
                if (Channel.isChannelPrefix(recipient.charAt(0))) {
                    onParseChannelMessage(nick, recipient, message);
                } else {
                    onParsePrivateMessage(nick, message);
                }
            }
        }
    }

    private void onParsePrivateMessage(final String nick, final String message) {
        final PrivateMessageUser sendingUser = mServer.getPrivateMessageUser(nick, message);
        mServer.onPrivateMessage(sendingUser, message, false);
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String message) {
        final ChannelUser sendingUser = mUserChannelInterface.getUserIfExists(sendingNick);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        // This occurs rarely - usually on BNCs - for example the ZNC buffer starts with a
        // PRIVMSG from the nick ***. Also if someone said something on the channel during
        // the buffer but is not in the channel now then this will also happen
        if (sendingUser == null) {
            mServerEventBus.onChannelMessage(mServer.getUser(), channel, sendingNick, message);
        } else {
            mServerEventBus.onChannelMessage(mServer.getUser(), channel, sendingUser, message);
        }
    }
}