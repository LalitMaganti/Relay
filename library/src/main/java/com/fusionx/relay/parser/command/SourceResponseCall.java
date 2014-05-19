package com.fusionx.relay.parser.command;

import com.fusionx.relay.call.Call;

public class SourceResponseCall extends Call {

    public SourceResponseCall(final String nick) {
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}
