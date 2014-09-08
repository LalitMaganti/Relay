package co.fusionx.relay.internal.parser.main.code;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.event.channel.ChannelInitialTopicEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class InitalTopicParser extends CodeParser {

    private String mTempTopic;

    public InitalTopicParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        super(server, userChannelInterface);
    }

    @Override
    public void onParseCode(final List<String> parsedArray, final int code) {
        if (code == ServerReplyCodes.RPL_TOPIC) {
            onTopic(parsedArray);
        } else if (code == ServerReplyCodes.RPL_TOPICWHOTIME) {
            onTopicInfo(parsedArray);
        }
    }

    private void onTopic(final List<String> parsedArray) {
        mTempTopic = parsedArray.get(1);
    }

    private void onTopicInfo(final List<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String nick = ParseUtils.getNickFromPrefix(parsedArray.get(1));
        final Optional<RelayChannel> optional = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optional, mServer);
        Optionals.ifPresent(optional, channel -> {
            channel.postAndStoreEvent(new ChannelInitialTopicEvent(channel, nick, mTempTopic));
            mTempTopic = null;
        });
    }
}