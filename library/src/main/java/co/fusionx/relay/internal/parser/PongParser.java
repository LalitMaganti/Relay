package co.fusionx.relay.internal.parser;

import java.util.List;

import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

public class PongParser extends CommandParser {

    public PongParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        // TODO - what should be done here?
    }
}