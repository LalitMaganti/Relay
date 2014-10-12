package co.fusionx.relay.parser.rfc;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.parser.ReplyCodeParser;

public class TopicCodeParser implements ReplyCodeParser {

    public final ObserverHelper<TopicCodeObserver> mObserverHelper = new ObserverHelper<>();

    public TopicCodeParser addObserver(final TopicCodeObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public TopicCodeParser addObservers(final Collection<? extends TopicCodeObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        if (code == ReplyCodes.RPL_TOPIC) {
            onTopic(parsedArray);
        } else if (code == ReplyCodes.RPL_TOPICWHOTIME) {
            onTopicInfo(parsedArray);
        }
    }

    @Override
    public List<Integer> parsableCodes() {
        return ImmutableList.of(ReplyCodes.RPL_TOPIC, ReplyCodes.RPL_TOPICWHOTIME);
    }

    private void onTopic(final List<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String topic = parsedArray.get(1);

        mObserverHelper.notifyObservers(new Consumer<TopicCodeObserver>() {
            @Override
            public void apply(final TopicCodeObserver observer) {
                observer.onTopic(channelName, topic);
            }
        });
    }

    private void onTopicInfo(final List<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String topicSetterPrefix = parsedArray.get(1);
        final String epochTime = parsedArray.get(2);

        mObserverHelper.notifyObservers(new Consumer<TopicCodeObserver>() {
            @Override
            public void apply(final TopicCodeObserver observer) {
                observer.onTopicInfo(channelName, topicSetterPrefix, epochTime);
            }
        });
    }

    public static interface TopicCodeObserver {

        public void onTopic(final String channelName, final String topic);

        public void onTopicInfo(final String channelName, final String topicSetterPrefix,
                final String epochTimeString);
    }
}