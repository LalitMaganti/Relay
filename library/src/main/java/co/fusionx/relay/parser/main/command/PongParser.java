package co.fusionx.relay.parser.main.command;

import java.util.List;

import co.fusionx.relay.base.relay.RelayServer;

public class PongParser extends CommandParser {

    public PongParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        // TODO - what should be done here?
    }
}