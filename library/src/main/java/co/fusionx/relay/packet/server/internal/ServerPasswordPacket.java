package co.fusionx.relay.packet.server.internal;

import co.fusionx.relay.packet.Packet;

public class ServerPasswordPacket implements Packet {

    private final String mPassword;

    public ServerPasswordPacket(final String password) {
        mPassword = password;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("PASS %s", mPassword);
    }
}
