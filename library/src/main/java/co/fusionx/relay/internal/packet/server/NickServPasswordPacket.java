package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class NickServPasswordPacket implements Packet {

    private final String mPassword;

    public NickServPasswordPacket(final String password) {
        mPassword = password;
    }

    @Override
    public String getLine() {
        return String.format("NICKSERV IDENTIFY %s", mPassword);
    }
}