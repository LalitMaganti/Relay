package co.fusionx.relay.parser.ircv3;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.rfc.JoinParser;

public class ExtendedJoinParser implements CommandParser {

    public final ExtendedJoinObserver mExtendedJoinObserver;

    public ExtendedJoinParser(final ExtendedJoinObserver extendedJoinObserver) {
        mExtendedJoinObserver = extendedJoinObserver;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        // We should have 2 parameters after the channel name
        if (parsedArray.size() == 3) {
            final String accountName = parsedArray.get(1);
            final String realName = parsedArray.get(2);

            mExtendedJoinObserver.onExtendedJoin(prefix, channelName, accountName, realName);
        } else {
            // Clearly we have used the extended join parser without actually having extended
            // join accepted/sent by the server
            mExtendedJoinObserver.onJoin(prefix, channelName);
        }
    }

    public static interface ExtendedJoinObserver extends JoinParser.JoinObserver {

        public void onExtendedJoin(final String prefix, final String channelName,
                final String accountName, final String realName);
    }
}