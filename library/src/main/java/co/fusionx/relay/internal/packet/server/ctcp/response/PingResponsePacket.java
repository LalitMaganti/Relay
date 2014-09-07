package co.fusionx.relay.internal.packet.server.ctcp.response;

public class PingResponsePacket extends CTCPResponsePacket {

    private final String mTimestamp;

    public PingResponsePacket(final String nick, final String timestamp) {
        super(nick);

        mTimestamp = timestamp;
    }

    @Override
    public String getResponse() {
        return String.format("PING %s", mTimestamp);
    }
}
