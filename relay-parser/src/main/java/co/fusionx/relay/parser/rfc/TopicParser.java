package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class TopicParser implements CommandParser {

    private final ObserverHelper<TopicObserver> mObserverHelper = new ObserverHelper<>();

    public TopicParser addObserver(final TopicObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public TopicParser addObservers(final Collection<? extends TopicObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);
        final String newTopic = parsedArray.get(1);

        mObserverHelper
                .notifyObservers(observer -> observer.onTopic(prefix, channelName, newTopic));
    }

    public static interface TopicObserver {

        public void onTopic(final String prefix, final String channelName, final String newTopic);
    }
}