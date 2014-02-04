package com.fusionx.relay.parser.code;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.ServerReplyCodes;
import com.fusionx.relay.event.channel.InitialTopicEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class TopicParser extends CodeParser {

    private String tempTopic;

    TopicParser(final Server server) {
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
        final Channel channel = mUserChannelInterface.getChannel(channelName);

        final InitialTopicEvent topicEvent = new InitialTopicEvent(channel, nick, tempTopic);
        mServerEventBus.postAndStoreEvent(topicEvent, channel);

        tempTopic = null;
    }
}