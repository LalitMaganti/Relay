package co.fusionx.relay.parser.ircv3;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.ParseUtils;

public class AwayParser implements CommandParser {

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (parsedArray.size() == 0) {
            // The user is no longer away
        } else {
            // The user is now away
            final String awayMessage = parsedArray.get(0);
        }
    }

    public static class PongParser implements CommandParser {

        @Override
        public void parseCommand(List<String> parsedArray, String prefix) {
            // TODO - what should be done here?
        }
    }
}
