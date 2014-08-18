package co.fusionx.relay.call.server.ctcp.response;

import co.fusionx.relay.call.Call;

public class PingResponseCall implements Call {

    private final String mRecipient;

    private final String mTimestamp;

    public PingResponseCall(final String nick, final String timestamp) {
        mRecipient = nick;
        mTimestamp = timestamp;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001PING %s\u0001", mRecipient, mTimestamp);
    }
}
