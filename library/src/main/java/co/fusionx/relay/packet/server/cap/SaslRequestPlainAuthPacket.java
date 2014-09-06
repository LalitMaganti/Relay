package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class SaslRequestPlainAuthPacket implements Packet {

    @Override
    public String getLine() {
        return "AUTHENTICATE PLAIN";
    }
}