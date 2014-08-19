package co.fusionx.relay.packet.server.ctcp.response;

import co.fusionx.relay.packet.Packet;

public class SourceResponsePacket implements Packet {

    public SourceResponsePacket(final String nick) {
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}
