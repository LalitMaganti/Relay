package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.internal.sender.packet.InternalPacketSender;

public class PingParser extends CommandParser {

    private final InternalPacketSender mInternalSender;

    public PingParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager, final PacketSender packetSender) {
        super(server, ucmanager, queryManager);

        mInternalSender = new InternalPacketSender(packetSender);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        // Immediately respond & return
        final String source = parsedArray.get(0);
        mInternalSender.pongServer(source);
    }
}