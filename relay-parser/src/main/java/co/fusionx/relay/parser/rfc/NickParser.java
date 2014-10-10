package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class NickParser implements CommandParser {

    private final NickObserver mNickObserver;

    public NickParser(final NickObserver nickObserver) {
        mNickObserver = nickObserver;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String newNick = parsedArray.get(0);
        mNickObserver.onNick(prefix, newNick);
    }

    public static interface NickObserver {

        public void onNick(final String prefix, final String newNick);
    }
}
