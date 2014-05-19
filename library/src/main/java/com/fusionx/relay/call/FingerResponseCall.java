package com.fusionx.relay.call;

import com.fusionx.relay.Server;
import com.fusionx.relay.call.Call;

public class FingerResponseCall extends Call {

    private final String mRecipient;

    private final String mFingerResponse;

    public FingerResponseCall(String nick, Server user) {
        mRecipient = nick;
        mFingerResponse = user.getServer().getConfiguration().getRealName();
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001FINGER :%s\u0001", mRecipient, mFingerResponse);
    }
}