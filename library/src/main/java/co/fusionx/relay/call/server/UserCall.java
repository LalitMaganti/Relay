package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class UserCall implements Call {

    private final String mUserName;

    private final String mRealName;

    public UserCall(final String userName, final String realName) {
        mUserName = userName;
        mRealName = realName;
    }

    @Override
    public String getLineToSendServer() {
        return "USER " + mUserName + " 8 * :" + mRealName;
    }
}