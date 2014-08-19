package co.fusionx.relay.packet.server.ctcp.response;

import co.fusionx.relay.packet.Packet;

public class PingResponsePacket implements Packet {

    private final String mRecipient;

    private final String mTimestamp;

    public PingResponsePacket(final String nick, final String timestamp) {
        mRecipient = nick;
        mTimestamp = timestamp;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001PING %s\u0001", mRecipient, mTimestamp);
    }
}
