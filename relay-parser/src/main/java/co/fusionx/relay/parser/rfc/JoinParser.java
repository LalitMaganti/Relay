package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class JoinParser implements CommandParser {

    private final JoinObserver mObserver;

    public JoinParser(final JoinObserver observer) {
        mObserver = observer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        mObserver.onJoin(prefix, channelName);
    }

    public static interface JoinObserver {

        public void onJoin(final String prefix, final String channelName);
    }
}