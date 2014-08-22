package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class CAPEndPacket implements Packet {

    @Override
    public String getLine() {
        return "CAP END";
    }
}