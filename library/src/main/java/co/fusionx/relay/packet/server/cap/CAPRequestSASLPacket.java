package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class CAPRequestSASLPacket implements Packet {

    @Override
    public String getLine() {
        return "CAP REQ : sasl multi-prefix";
    }
}