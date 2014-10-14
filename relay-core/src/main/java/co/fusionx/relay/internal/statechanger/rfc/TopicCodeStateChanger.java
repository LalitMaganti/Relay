package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.event.channel.ChannelInitialTopicEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.rfc.TopicCodeParser;
import co.fusionx.relay.internal.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class TopicCodeStateChanger implements TopicCodeParser.TopicCodeObserver {

    private final InternalUserChannelGroup mUserChannelInterface;

    private final InternalServer mServer;

    private final Map<String, String> mTopicMap;

    public TopicCodeStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface) {
        mServer = server;
        mUserChannelInterface = userChannelInterface;

        mTopicMap = new HashMap<>();
    }

    @Override
    public void onTopic(final String channelName, final String topic) {
        mTopicMap.put(channelName, topic);
    }

    @Override
    public void onTopicInfo(final String channelName, final String topicSetterPrefix,
            final String epochTimeString) {
        final String nick = ParseUtils.getNickFromPrefix(topicSetterPrefix);
        final Optional<InternalChannel> optional = mUserChannelInterface.getChannel(channelName);
        final String topic = mTopicMap.get(channelName);

        mTopicMap.remove(channelName);

        Optionals.run(optional,
                channel -> channel.postEvent(new ChannelInitialTopicEvent(channel, nick, topic)),
                () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }
}