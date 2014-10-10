package co.fusionx.relay.internal.packet.server.cap;

import co.fusionx.relay.internal.packet.Packet;

public class CAPEndPacket implements Packet {

    @Override
    public String getLine() {
        return "CAP END";
    }
}