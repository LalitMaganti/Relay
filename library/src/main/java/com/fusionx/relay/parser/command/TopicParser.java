package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.TopicEvent;

import java.util.List;

public class TopicParser extends CommandParser {

    public TopicParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final WorldUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannelIfExists(parsedArray.get(2));
        final String newTopic = parsedArray.get(3);

        final ChannelEvent event = new TopicEvent(channel, user, newTopic);
        mServerEventBus.postAndStoreEvent(event, channel);
    }
}