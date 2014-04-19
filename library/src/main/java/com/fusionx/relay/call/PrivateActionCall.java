package com.fusionx.relay.call;

public class PrivateActionCall extends Call {

    public final String userNick;

    public final String message;

    public PrivateActionCall(String userNick, String message) {
        this.userNick = userNick;
        this.message = message;
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}