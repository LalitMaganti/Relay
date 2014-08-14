package com.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelTopicEvent;
import com.fusionx.relay.util.LogUtils;
import com.fusionx.relay.function.Optionals;

import java.util.List;

public class TopicParser extends CommandParser {

    public TopicParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Optional<RelayChannel> optChan = mUserChannelInterface.getChannel(parsedArray.get(2));

        LogUtils.logOptionalBug(optChan, mServer);
        Optionals.ifPresent(optChan, channel -> {
            final String newTopic = parsedArray.get(3);

            final ChannelEvent event = new ChannelTopicEvent(channel, user, newTopic);
            mServerEventBus.postAndStoreEvent(event, channel);
        });
    }
}