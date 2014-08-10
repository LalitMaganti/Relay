package com.fusionx.relay.parser.command;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelTopicEvent;
import com.fusionx.relay.util.LogUtils;

import java.util.List;

import java8.util.Optional;

public class TopicParser extends CommandParser {

    public TopicParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Optional<RelayChannel> optChan = mUserChannelInterface.getChannel(parsedArray.get(2));

        LogUtils.logOptionalBug(optChan);
        optChan.ifPresent(channel -> {
            final String newTopic = parsedArray.get(3);

            final ChannelEvent event = new ChannelTopicEvent(channel, user, newTopic);
            mServerEventBus.postAndStoreEvent(event, channel);
        });
    }
}