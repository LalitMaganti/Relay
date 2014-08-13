package com.fusionx.relay.parser.code;

import com.google.common.base.Optional;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.constants.ServerReplyCodes;
import com.fusionx.relay.event.channel.ChannelInitialTopicEvent;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.LogUtils;
import com.fusionx.relay.util.Optionals;

import java.util.List;

class TopicParser extends CodeParser {

    private String tempTopic;

    TopicParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCode(final int code, final List<String> parsedArray) {
        if (code == ServerReplyCodes.RPL_TOPIC) {
            onTopic(parsedArray);
        } else if (code == ServerReplyCodes.RPL_TOPICWHOTIME) {
            onTopicInfo(parsedArray);
        }
    }

    private void onTopic(final List<String> parsedArray) {
        tempTopic = parsedArray.get(1);
    }

    private void onTopicInfo(final List<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String nick = IRCUtils.getNickFromRaw(parsedArray.get(1));
        final Optional<RelayChannel> optional = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optional, mServer);
        Optionals.ifPresent(optional, channel -> {
            final ChannelInitialTopicEvent topicEvent = new ChannelInitialTopicEvent(channel, nick,
                    tempTopic);
            mServerEventBus.postAndStoreEvent(topicEvent, channel);

            tempTopic = null;
        });
    }
}