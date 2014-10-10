package co.fusionx.relay.internal.packet.server.cap;

import co.fusionx.relay.internal.packet.Packet;

public class CAPRequestSASLPacket implements Packet {

    @Override
    public String getLine() {
        return "CAP REQ :sasl";
    }
}