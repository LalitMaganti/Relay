package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.WorldJoinEvent;
import com.fusionx.relay.event.server.JoinEvent;
import com.fusionx.relay.event.server.ServerEvent;

import java.util.List;

class JoinParser extends CommandParser {

    public JoinParser(final Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);

        final WorldUser user = getUserChannelInterface().getUserFromRaw(rawSource);
        Channel channel = getUserChannelInterface().getChannel(channelName);
        if (channel == null) {
            channel = getUserChannelInterface().getNewChannel(channelName);
        } else if (user.isUserNickEqual(getServer().getUser())) {
            channel.wipeChannelData();
        }
        getUserChannelInterface().coupleUserAndChannel(user, channel);

        if (user.isUserNickEqual(getServer().getUser())) {
            final ServerEvent event = new JoinEvent(channel);
            getServerEventBus().postAndStoreEvent(event);
        } else {
            final ChannelEvent event = new WorldJoinEvent(channel, user);
            getServerEventBus().postAndStoreEvent(event, channel);
        }
    }
}