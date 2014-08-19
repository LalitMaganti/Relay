package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class CAPRequestSASLPacket implements Packet {

    @Override
    public String getLineToSendServer() {
        return "CAP REQ : sasl multi-prefix";
    }
}