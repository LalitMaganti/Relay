package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.internal.sender.packet.InternalPacketSender;

public class PingParser extends CommandParser {

    private final InternalPacketSender mInternalSender;

    public PingParser(final RelayServer server,
            final RelayUserChannelGroup ucmanager,
            final RelayQueryUserGroup queryManager, final PacketSender packetSender) {
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