package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.VersionEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class CtcpParser extends CommandParser {

    public CtcpParser(Server server) {
        super(server);
    }

    public static boolean isCtcpCommand(final String message) {
        return message.startsWith("\u0001") && message.endsWith("\u0001");
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            onAction(parsedArray, rawSource);
        } else if (message.startsWith("VERSION")) {
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            // TODO - get the version from the app
            mServer.getServerCallBus().post(new VersionEvent(nick, mServer.toString()));
        }
    }

    private void onAction(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final String action = parsedArray.get(3).replace("ACTION ", "");

        if (!InterfaceHolders.getPreferences().shouldIgnoreUser(nick)) {
            final String recipient = parsedArray.get(2);
            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                onParseChannelAction(recipient, nick, action);
            } else {
                final PrivateMessageUser sendingUser = mServer.getPrivateMessageUser(nick, action);
                mServer.onPrivateAction(sendingUser, action, false);
            }
        }
    }

    private void onParseChannelAction(final String channelName, final String userNick,
            final String action) {
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        final ChannelUser sendingUser = mUserChannelInterface.getUserIfExists(userNick);
        // This occurs rarely - usually on ZNCs. Also if someone performed an action on the
        // channel during the buffer but is not in the channel now then this will also happen
        if (sendingUser == null) {
            mServerEventBus.onChannelAction(mServer.getUser(), channel, userNick, action);
        } else {
            mServerEventBus.onChannelAction(mServer.getUser(), channel, sendingUser, action);
        }
    }
}