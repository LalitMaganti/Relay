package co.fusionx.relay.parser.code;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.channel.ChannelInitialTopicEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.LogUtils;

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
            channel.postAndStoreEvent(new ChannelInitialTopicEvent(channel, nick, tempTopic));
            tempTopic = null;
        });
    }
}