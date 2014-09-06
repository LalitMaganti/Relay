package co.fusionx.relay.packet.server.cap;

import co.fusionx.relay.packet.Packet;

public class CapRequestCapabilitiesPacket implements Packet {

    private final String mCapabilities;

    public CapRequestCapabilitiesPacket(final String capabilities) {
        mCapabilities = capabilities;
    }

    @Override
    public String getLine() {
        return String.format("CAP REQ :%1$s", mCapabilities);
    }
}
