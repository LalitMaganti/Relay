package com.fusionx.relay.call;

public class RawCall extends Call {

    public final String rawLine;

    public RawCall(String rawLine) {
        this.rawLine = rawLine;
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}