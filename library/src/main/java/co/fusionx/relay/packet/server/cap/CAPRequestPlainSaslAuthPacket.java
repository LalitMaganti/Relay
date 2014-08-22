package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class CAPRequestPlainSaslAuthPacket implements Packet {

    @Override
    public String getLine() {
        return "AUTHENTICATE PLAIN";
    }
}