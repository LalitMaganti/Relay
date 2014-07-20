package com.fusionx.relay.parser.command;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelTopicEvent;

import java.util.List;

public class TopicParser extends CommandParser {

    public TopicParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final ChannelUser user = getUserChannelInterface().getUserFromRaw(rawSource);
        final RelayChannel channel = getUserChannelInterface().getChannel(parsedArray.get(2));
        final String newTopic = parsedArray.get(3);

        final ChannelEvent event = new ChannelTopicEvent(channel, user, newTopic);
        getServerEventBus().postAndStoreEvent(event, channel);
    }
}