package co.fusionx.relay.internal.packet.server.ctcp.response;

public class FingerResponsePacket extends CTCPResponsePacket {

    private final String mFingerResponse;

    public FingerResponsePacket(final String nick, final String realName) {
        super(nick);

        mFingerResponse = realName;
    }

    @Override
    public String getResponse() {
        return String.format("FINGER %s", mFingerResponse);
    }
}
