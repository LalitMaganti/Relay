package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.ChannelWorldUserEvent;

import java.util.List;

public abstract class RemoveUserParser extends CommandParser {

    RemoveUserParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);
        final Channel channel = getUserChannelInterface().getChannel(channelName);
        final WorldUser removedUser = getRemovedUser(parsedArray, rawSource);

        if (removedUser.getNick().equals(getServer().getUser().getNick())) {
            onRemoved(parsedArray, rawSource, channel);
        } else {
            onUserRemoved(parsedArray, rawSource, channel, removedUser);
        }
    }

    abstract WorldUser getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract ChannelWorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final Channel channel, final WorldUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final Channel channel);

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final Channel channel, final WorldUser removedUser) {
        getUserChannelInterface().decoupleUserAndChannel(removedUser, channel);

        final ChannelWorldUserEvent event = getEvent(parsedArray, rawSource, channel, removedUser);
        getServerEventBus().postAndStoreEvent(event, channel);
    }
}