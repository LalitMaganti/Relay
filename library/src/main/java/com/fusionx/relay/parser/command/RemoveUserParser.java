package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;

import java.util.List;

public abstract class RemoveUserParser extends CommandParser {

    public RemoveUserParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);
        final Channel channel = mUserChannelInterface.getChannel(channelName);

        final ChannelUser removedUser = getRemovedUser(parsedArray, rawSource);

        if (removedUser.isUserNickEqual(mServer.getUser())) {
            onRemoved(parsedArray, rawSource, channel);
        } else {
            onUserRemoved(parsedArray, channel, removedUser);
        }
    }

    private void onUserRemoved(final List<String> parsedArray, final Channel channel,
            final ChannelUser user) {
        final String message = getUserRemoveMessage(parsedArray, channel, user);

        // Decrease the user count before we broadcast the message so that it is picked up
        channel.onDecrementUserType(user.getChannelPrivileges(channel));

        if (channel.isObserving()) {
            user.onRemove(channel);
            mUserChannelInterface.removeChannelFromUser(channel, user);
            mServerEventBus.sendGenericChannelEvent(channel, message, UserListChangeType.REMOVE,
                    user);
        } else {
            mUserChannelInterface.decoupleUserAndChannel(user, channel);
            mServerEventBus.sendGenericChannelEvent(channel, message, UserListChangeType.REMOVE);
        }
    }

    abstract ChannelUser getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract String getUserRemoveMessage(final List<String> parsedArray,
            final Channel channel, final ChannelUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final Channel channel);
}