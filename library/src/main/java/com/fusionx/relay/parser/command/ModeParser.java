package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class ModeParser extends CommandParser {

    public ModeParser(Server server) {
        super(server);
    }

    // TODO - split this up
    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String mode = parsedArray.get(3);

        if (Channel.isChannelPrefix(recipient.charAt(0))) {
            // The recipient is a channel (i.e. the mode of a user in the channel is being changed
            // or possibly the mode of the channel itself)
            final Channel channel = mUserChannelInterface.getChannel(recipient);
            final int messageLength = parsedArray.size();
            if (messageLength == 4) {
                // User not specified - therefore channel mode is being changed
                // TODO - implement this?
            } else if (messageLength == 5) {
                // User specified - therefore user mode in channel is being changed
                final String nick = IRCUtils.getNickFromRaw(parsedArray.get(4));
                final ChannelUser user = mUserChannelInterface.getUserIfExists(nick);
                if (user != null) {
                    final String message = user.onModeChange(sendingUser, channel, mode);
                    mServerEventBus.sendGenericChannelEvent(channel, message,
                            UserListChangeType.MODIFIED);
                } else {
                    new Event("");
                }
            } else {
                IRCUtils.removeFirstElementFromList(parsedArray, 4);
                final ChannelUser user = mUserChannelInterface.getUserIfExists(sendingUser);
                final String nick = (user == null) ? sendingUser : user.getPrettyNick(channel);
                final String message = mEventResponses.getModeChangedMessage(mode,
                        IRCUtils.concatStringList(parsedArray), nick);
                mServerEventBus.sendGenericChannelEvent(channel, message,
                        UserListChangeType.MODIFIED);
            }
        } else {
            // A user is changing a mode about themselves
            // TODO - implement this?
        }
    }
}