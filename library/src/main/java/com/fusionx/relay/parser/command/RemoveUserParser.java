package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.WorldUserEvent;

import java.util.List;

public abstract class RemoveUserParser extends CommandParser {

    public RemoveUserParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        final WorldUser removedUser = getRemovedUser(parsedArray, rawSource);

        if (removedUser.isUserNickEqual(mServer.getUser())) {
            onRemoved(parsedArray, rawSource, channel);
        } else {
            onUserRemoved(parsedArray, rawSource, channel, removedUser);
        }
    }

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final Channel channel, final WorldUser removedUser) {
        // Decrease the user count before we broadcast the message so that it is picked up
        channel.onDecrementUserType(removedUser.getChannelPrivileges(channel));
        mUserChannelInterface.decoupleUserAndChannel(removedUser, channel);

        final WorldUserEvent event = getEvent(parsedArray, rawSource, channel, removedUser);
        mServerEventBus.postAndStoreEvent(event, channel);
    }

    abstract WorldUser getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract WorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final Channel channel, final WorldUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final Channel channel);
}