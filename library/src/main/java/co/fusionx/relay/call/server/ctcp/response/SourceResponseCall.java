package co.fusionx.relay.call.server.ctcp.response;

import co.fusionx.relay.call.Call;

public class SourceResponseCall implements Call {

    public SourceResponseCall(final String nick) {
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}
