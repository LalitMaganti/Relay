package co.fusionx.relay.parser.rfc;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class PartParser implements CommandParser {

    private final PartObserver mPartObserver;

    public PartParser(final PartObserver partObserver) {
        mPartObserver = partObserver;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);
        final String reason = parsedArray.size() == 2 ? parsedArray.get(1).replace("\"", "") : null;

        final Optional<String> optionalReason = Optional.fromNullable(reason);

        mPartObserver.onPart(prefix, channelName, optionalReason);
    }

    public static interface PartObserver {

        public void onPart(final String prefix, final String channelName,
                final Optional<String> optionalReason);
    }
}