package co.fusionx.relay.packet.server.ctcp.response;

import co.fusionx.relay.packet.Packet;

public class VersionResponsePacket implements Packet {

    private final String mRecipient;

    public VersionResponsePacket(final String askingUser) {
        mRecipient = askingUser;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001VERSION :%s\u0001", mRecipient, "Relay:1.0:Android");
    }
}