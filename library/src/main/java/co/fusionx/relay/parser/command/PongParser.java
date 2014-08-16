package co.fusionx.relay.parser.command;

import java.util.List;

import co.fusionx.relay.RelayServer;

public class PongParser extends CommandParser {

    public PongParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        // TODO - what should be done here?
    }
}