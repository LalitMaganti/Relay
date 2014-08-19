package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class CAPSupportedPacket implements Packet {

    @Override
    public String getLineToSendServer() {
        return "CAP LS";
    }
}
