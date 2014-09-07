package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.util.ParseUtils;

public class AwayParser extends CommandParser {

    public AwayParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (parsedArray.size() == 0) {
            // The user is no longer away
        } else {
            // The user is now away
            final String awayMessage = parsedArray.get(0);
        }
    }
}
