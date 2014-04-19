package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.WorldPartEvent;
import com.fusionx.relay.event.channel.WorldUserEvent;
import com.fusionx.relay.event.server.PartEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class PartParser extends RemoveUserParser {

    public PartParser(Server server) {
        super(server);
    }

    @Override
    public WorldUser getRemovedUser(final List<String> parsedArray, final String rawSource) {
        final String userNick = IRCUtils.getNickFromRaw(rawSource);
        return getUserChannelInterface().getUserIfExists(userNick);
    }

    @Override
    public WorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final Channel channel, final WorldUser user) {
        final String reason = parsedArray.size() == 4 ? parsedArray.get(3).replace("\"", "") : "";
        return new WorldPartEvent(channel, user, reason);
    }

    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource, final Channel channel) {
        getUserChannelInterface().removeChannel(channel);

        final PartEvent event = new PartEvent(channel);
        getServerEventBus().postAndStoreEvent(event);
    }
}