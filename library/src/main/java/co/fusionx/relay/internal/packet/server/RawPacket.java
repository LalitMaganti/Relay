package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class RawPacket implements Packet {

    public final String rawLine;

    public RawPacket(String rawLine) {
        this.rawLine = rawLine;
    }

    @Override
    public String getLine() {
        return rawLine;
    }
}