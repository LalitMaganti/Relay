package co.fusionx.relay.call.server;

import co.fusionx.relay.Server;
import co.fusionx.relay.call.Call;

public class FingerResponseCall extends Call {

    private final String mRecipient;

    private final String mFingerResponse;

    public FingerResponseCall(final String nick, final Server server) {
        mRecipient = nick;
        mFingerResponse = server.getConfiguration().getRealName();
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001FINGER :%s\u0001", mRecipient, mFingerResponse);
    }
}