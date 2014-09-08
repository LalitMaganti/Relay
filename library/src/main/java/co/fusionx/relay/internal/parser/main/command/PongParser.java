package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;

public class PongParser extends CommandParser {

    public PongParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        super(server, userChannelInterface);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        // TODO - what should be done here?
    }
}