package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class SourceResponseCall extends Call {

    public SourceResponseCall(final String nick) {
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}
