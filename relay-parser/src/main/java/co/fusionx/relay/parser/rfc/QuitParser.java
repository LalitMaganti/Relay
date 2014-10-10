package co.fusionx.relay.parser.rfc;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class QuitParser implements CommandParser {

    private final QuitObserver mQuitObserver;

    public QuitParser(final QuitObserver quitObserver) {
        mQuitObserver = quitObserver;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String reason = parsedArray.size() == 1 ? parsedArray.get(0).replace("\"", "") : null;

        final Optional<String> optionalReason = Optional.fromNullable(reason);

        mQuitObserver.onQuit(prefix, optionalReason);
    }

    public static interface QuitObserver {

        public void onQuit(final String prefix, final Optional<String> optionalReason);
    }
}