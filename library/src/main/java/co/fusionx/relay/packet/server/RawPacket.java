package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

public class RawPacket implements Packet {

    public final String rawLine;

    public RawPacket(String rawLine) {
        this.rawLine = rawLine;
    }

    @Override
    public String getLineToSendServer() {
        return rawLine;
    }
}