package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class NickServPasswordCall implements Call {

    private final String mPassword;

    public NickServPasswordCall(final String password) {
        mPassword = password;
    }

    @Override
    public String getLineToSendServer() {
        return "NICKSERV IDENTIFY " + mPassword;
    }
}