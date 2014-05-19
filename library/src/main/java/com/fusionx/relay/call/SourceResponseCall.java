package com.fusionx.relay.call;

import com.fusionx.relay.call.Call;

public class SourceResponseCall extends Call {

    public SourceResponseCall(final String nick) {
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}
