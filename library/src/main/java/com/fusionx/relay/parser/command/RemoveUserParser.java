package com.fusionx.relay.parser.command;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelWorldUserEvent;

import java.util.List;

public abstract class RemoveUserParser extends CommandParser {

    RemoveUserParser(RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);
        final RelayChannel channel = getUserChannelInterface().getChannel(channelName);
        final RelayChannelUser removedUser = getRemovedUser(parsedArray, rawSource);

        if (getServer().getUser().isNickEqual(removedUser.getNick().getNickAsString())) {
            onRemoved(parsedArray, rawSource, channel);
        } else {
            onUserRemoved(parsedArray, rawSource, channel, removedUser);
        }
    }

    abstract RelayChannelUser getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract ChannelWorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel, final ChannelUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel);

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel, final RelayChannelUser removedUser) {
        getUserChannelInterface().decoupleUserAndChannel(removedUser, channel);

        final ChannelWorldUserEvent event = getEvent(parsedArray, rawSource, channel, removedUser);
        getServerEventBus().postAndStoreEvent(event, channel);
    }
}