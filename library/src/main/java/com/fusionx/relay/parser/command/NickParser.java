package com.fusionx.relay.parser.command;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;

import java.util.List;
import java.util.Set;

public class NickParser extends CommandParser {

    public NickParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Set<Channel> channels = user.getChannels();
        final String oldNick = user.getColorfulNick();

        user.setNick(parsedArray.get(2));

        final String message = mEventResponses.getNickChangedMessage(oldNick,
                user.getColorfulNick(), user instanceof AppUser);

        mServerEventBus.sendGenericServerEvent(mServer, message);

        for (final Channel channel : channels) {
            channel.getUsers().update(user, channel);

            if (channel.isObserving()) {
                mServerEventBus.sendGenericChannelEvent(channel, message,
                        UserListChangeType.MODIFIED, user);
            } else {
                mServerEventBus.sendGenericChannelEvent(channel, message,
                        UserListChangeType.MODIFIED);
            }
        }
    }
}