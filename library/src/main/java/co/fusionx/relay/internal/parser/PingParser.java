package co.fusionx.relay.internal.parser;

import java.util.List;

import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;

public class PingParser extends CommandParser {

    private final InternalSender mInternalSender;

    public PingParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager, final PacketSender packetSender) {
        super(server, ucmanager, queryManager);

        mInternalSender = new RelayInternalSender(packetSender);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        // Immediately respond & return
        final String source = parsedArray.get(0);
        mInternalSender.pongServer(source);
    }
}