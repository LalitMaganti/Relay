package co.fusionx.relay.internal.packet.server.internal;

import co.fusionx.relay.internal.packet.Packet;

public class ServerPasswordPacket implements Packet {

    private final String mPassword;

    public ServerPasswordPacket(final String password) {
        mPassword = password;
    }

    @Override
    public String getLine() {
        return String.format("PASS %s", mPassword);
    }
}
