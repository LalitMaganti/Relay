package co.fusionx.relay.packet.server.ctcp.response;

import co.fusionx.relay.packet.Packet;

public class ERRMSGResponsePacket implements Packet {

    private final String mRecipient;

    private final String mQuery;

    public ERRMSGResponsePacket(final String nick, final String query) {
        mRecipient = nick;
        mQuery = query;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001ERRMSG %s :%s\u0001", mRecipient, mQuery,
                "No error occured");
    }
}
