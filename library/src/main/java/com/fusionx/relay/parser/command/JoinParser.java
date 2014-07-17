package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import com.fusionx.relay.event.server.JoinEvent;
import com.fusionx.relay.event.server.ServerEvent;

import java.util.List;

class JoinParser extends CommandParser {

    private static final int CHANNEL_NAME_INDEX = 2;

    public JoinParser(final Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(CHANNEL_NAME_INDEX);

        // Retrieve the user and channel
        final ChannelUser user = getUserChannelInterface().getUserFromRaw(rawSource);
        Channel channel = getUserChannelInterface().getChannel(channelName);

        // Store whether the user is the app user
        final boolean appUser = getServer().getUser().isNickEqual(user.getNick().getNickAsString());
        if (channel == null) {
            // If the channel is null then we haven't joined it before (disconnection) and we should
            // create a new channel
            channel = getUserChannelInterface().getNewChannel(channelName);
        } else if (appUser) {
            // If the channel is not null then we simply clear the data of the channel
            channel.clearInternalData();
        }
        // Put the user and channel together
        getUserChannelInterface().coupleUserAndChannel(user, channel);

        // Post the event to the channel
        final ChannelEvent event = new ChannelWorldJoinEvent(channel, user);
        getServerEventBus().postAndStoreEvent(event, channel);

        // Also post a server event if the user who joined was the app user
        if (appUser) {
            final ServerEvent joinEvent = new JoinEvent(channel);
            getServerEventBus().postAndStoreEvent(joinEvent);
        }
    }
}