package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class UserCall extends Call {

    private final String userName;

    private final String realName;

    public UserCall(final String userName, final String realName) {
        this.userName = userName;
        this.realName = realName;
    }

    @Override
    public String getLineToSendServer() {
        return "USER " + userName + " 8 * :" + realName;
    }
}