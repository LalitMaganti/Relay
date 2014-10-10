package co.fusionx.relay.internal.packet.server.ctcp.response;

public class ERRMSGResponsePacket extends CTCPResponsePacket {

    private final String mQuery;

    public ERRMSGResponsePacket(final String nick, final String query) {
        super(nick);
        mQuery = query;
    }

    @Override
    public String getResponse() {
        return String.format("ERRMSG %s :%s", mQuery, "No error occured");
    }
}
