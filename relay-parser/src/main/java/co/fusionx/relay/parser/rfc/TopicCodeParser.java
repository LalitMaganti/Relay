package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.util.ParseUtils;

public class TopicCodeParser implements ReplyCodeParser {

    private final TopicCodeObserver mTopicCodeObserver;

    public TopicCodeParser(final TopicCodeObserver topicCodeObserver) {
        mTopicCodeObserver = topicCodeObserver;
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        if (code == ReplyCodes.RPL_TOPIC) {
            onTopic(parsedArray);
        } else if (code == ReplyCodes.RPL_TOPICWHOTIME) {
            onTopicInfo(parsedArray);
        }
    }

    private void onTopic(final List<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String topic = parsedArray.get(1);

        mTopicCodeObserver.onTopic(channelName, topic);
    }

    private void onTopicInfo(final List<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String topicSetterPrefix = parsedArray.get(1);
        final String epochTime = parsedArray.get(2);

        mTopicCodeObserver.onTopicInfo(channelName, topicSetterPrefix, epochTime);
    }

    public static interface TopicCodeObserver {

        public void onTopic(final String channelName, final String topic);

        public void onTopicInfo(final String channelName, final String topicSetterPrefix,
                final String epochTimeString);
    }
}