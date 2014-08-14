package com.fusionx.relay.call.server;

import com.fusionx.relay.call.Call;

public class RawCall extends Call {

    public final String rawLine;

    public RawCall(String rawLine) {
        this.rawLine = rawLine;
    }

    @Override
    public String getLineToSendServer() {
        return rawLine;
    }
}