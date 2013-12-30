package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;

import java.util.List;

public class JoinParser extends CommandParser {

    public JoinParser(final Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannel(parsedArray.get(2));

        if (user.isUserNickEqual(mServer.getUser())) {
            mUserChannelInterface.coupleUserAndChannel(user, channel);
            mServerEventBus.onChannelJoined(channel.getName());
        } else {
            final String message = mEventResponses.getJoinMessage(user.getPrettyNick(channel));
            if (channel.isObserving()) {
                user.onJoin(channel);
                mUserChannelInterface.addChannelToUser(user, channel);
                mServerEventBus.sendGenericChannelEvent(channel, message,
                        UserListChangeType.ADD, user);
            } else {
                mUserChannelInterface.coupleUserAndChannel(user, channel);
                mServerEventBus.sendGenericChannelEvent(channel, message,
                        UserListChangeType.ADD);
            }
        }
    }
}