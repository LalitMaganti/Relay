package com.fusionx.relay.call.server;

import com.fusionx.relay.call.Call;

public class ERRMSGResponseCall extends Call {

    private final String mRecipient;

    private final String mQuery;

    public ERRMSGResponseCall(final String nick, final String query) {
        mRecipient = nick;
        mQuery = query;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("NOTICE %s \u0001ERRMSG %s :%s\u0001", mRecipient, mQuery,
                "No error occured");
    }
}
