package com.fusionx.relay.call.user;

public class PrivateMessageCall {

    public final String userNick;

    public final String message;

    public PrivateMessageCall(String userNick, String message) {
        this.userNick = userNick;
        this.message = message;
    }
}