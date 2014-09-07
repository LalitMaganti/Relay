package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;

public class PongParser extends CommandParser {

    public PongParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        // TODO - what should be done here?
    }
}