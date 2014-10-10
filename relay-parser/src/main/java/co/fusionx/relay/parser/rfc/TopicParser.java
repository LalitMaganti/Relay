package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class TopicParser implements CommandParser {

    private final TopicObserver mObserver;

    public TopicParser(final TopicObserver observer) {
        mObserver = observer;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);
        final String newTopic = parsedArray.get(1);

        mObserver.onTopic(prefix, channelName, newTopic);
    }

    public static interface TopicObserver {

        public void onTopic(final String prefix, final String channelName, final String newTopic);
    }
}