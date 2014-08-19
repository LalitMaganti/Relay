package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

public class NickServPasswordPacket implements Packet {

    private final String mPassword;

    public NickServPasswordPacket(final String password) {
        mPassword = password;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NICKSERV IDENTIFY %s", mPassword);
    }
}