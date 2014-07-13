package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelPartEvent;
import com.fusionx.relay.event.channel.ChannelWorldPartEvent;
import com.fusionx.relay.event.channel.ChannelWorldUserEvent;
import com.fusionx.relay.event.server.PartEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class PartParser extends RemoveUserParser {

    public PartParser(Server server) {
        super(server);
    }

    @Override
    public ChannelUser getRemovedUser(final List<String> parsedArray, final String rawSource) {
        final String userNick = IRCUtils.getNickFromRaw(rawSource);
        return getUserChannelInterface().getUserIfExists(userNick);
    }

    @Override
    public ChannelWorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final Channel channel, final ChannelUser user) {
        final String reason = parsedArray.size() == 4 ? parsedArray.get(3).replace("\"", "") : "";
        return new ChannelWorldPartEvent(channel, user, reason);
    }

    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource, final Channel channel) {
        // ZNCs can be stupid and can sometimes send PART commands for channels they didn't send
        // JOIN commands for...
        if (channel != null) {
            final ChannelPartEvent partEvent = new ChannelPartEvent(channel);
            getServerEventBus().postAndStoreEvent(partEvent, channel);

            getUserChannelInterface().removeChannel(channel);

            final PartEvent event = new PartEvent(channel);
            getServerEventBus().postAndStoreEvent(event);
        }
    }
}