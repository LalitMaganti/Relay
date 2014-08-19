package co.fusionx.relay.packet.server.ctcp.response;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.packet.Packet;

public class FingerResponsePacket implements Packet {

    private final String mRecipient;

    private final String mFingerResponse;

    public FingerResponsePacket(final String nick, final Server server) {
        mRecipient = nick;
        mFingerResponse = server.getConfiguration().getRealName();
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001FINGER :%s\u0001", mRecipient, mFingerResponse);
    }
}