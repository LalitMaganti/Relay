package co.fusionx.relay.call.server.internal;

import co.fusionx.relay.call.Call;

public class PongCall implements Call {

    public final String mSource;

    public PongCall(final String source) {
        mSource = source;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("PONG %s", mSource);
    }
}