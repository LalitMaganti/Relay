package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;

public class PongParser extends CommandParser {

    public PongParser(final RelayServer server,
            final RelayUserChannelGroup ucmanager,
            final RelayQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        // TODO - what should be done here?
    }
}