package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;

public class PingParser extends CommandParser {

    private final RelayInternalSender mInternalSender;

    public PingParser(final RelayServer server, final RelayUserChannelDao dao,
            final BaseSender sender) {
        super(server, dao);

        mInternalSender = new RelayInternalSender(sender);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        // Immediately respond & return
        final String source = parsedArray.get(0);
        mInternalSender.pongServer(source);
    }
}