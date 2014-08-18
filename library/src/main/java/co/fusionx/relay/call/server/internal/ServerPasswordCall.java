package co.fusionx.relay.call.server.internal;

import co.fusionx.relay.call.Call;

public class ServerPasswordCall implements Call {

    private final String mPassword;

    public ServerPasswordCall(final String password) {
        mPassword = password;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("PASS %s", mPassword);
    }
}
