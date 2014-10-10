package co.fusionx.relay.internal.packet.server.cap;

import co.fusionx.relay.internal.packet.Packet;

public class CAPLSPacket implements Packet {

    @Override
    public String getLine() {
        return "CAP LS";
    }
}
