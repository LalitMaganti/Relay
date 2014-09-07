package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.packet.server.JoinPacket;
import co.fusionx.relay.internal.packet.server.NickChangePacket;
import co.fusionx.relay.internal.packet.server.RawPacket;
import co.fusionx.relay.internal.packet.server.WhoisPacket;
import co.fusionx.relay.sender.ServerSender;

public class RelayServerSender implements ServerSender {

    private final RelayServer mServer;

    private final RelayPacketSender mCallHandler;

    public RelayServerSender(final RelayServer server, final RelayPacketSender callHandler) {
        mServer = server;
        mCallHandler = callHandler;
    }

    @Override
    public void sendQuery(final String nick, final String message) {
        // This is invalid - we don't have anything to send the server directly
    }

    @Override
    public void sendJoin(final String channelName) {
        mCallHandler.sendPacket(new JoinPacket(channelName));
    }

    @Override
    public void sendNick(final String newNick) {
        mCallHandler.sendPacket(new NickChangePacket(newNick));
    }

    @Override
    public void sendWhois(final String nick) {
        mCallHandler.sendPacket(new WhoisPacket(nick));
    }

    @Override
    public void sendRawLine(final String rawLine) {
        mCallHandler.sendPacket(new RawPacket(rawLine));
    }
}
