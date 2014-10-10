package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class PingParser implements CommandParser {

    private final PingObserver mObserver;

    public PingParser(final PingObserver observer) {
        mObserver = observer;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String serverHostname = parsedArray.get(0);
        mObserver.onPing(serverHostname);
    }

    public static interface PingObserver {

        public void onPing(final String serverHostname);
    }
}