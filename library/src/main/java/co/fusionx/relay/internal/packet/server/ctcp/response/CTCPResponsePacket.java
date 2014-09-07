package co.fusionx.relay.internal.packet.server.ctcp.response;

import co.fusionx.relay.internal.packet.Packet;

public abstract class CTCPResponsePacket implements Packet {

    private final String mRecipient;

    public CTCPResponsePacket(final String recipient) {
        mRecipient = recipient;
    }

    @Override
    public String getLine() {
        return String.format("NOTICE %s \u0001%s\u0001", mRecipient, getResponse());
    }

    public abstract String getResponse();
}