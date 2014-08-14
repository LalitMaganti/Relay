package com.fusionx.relay.call.server;

import com.fusionx.relay.call.Call;

public class WhoisCall extends Call {

    public final String nick;

    public WhoisCall(String nick) {
        this.nick = nick;
    }

    @Override
    public String getLineToSendServer() {
        return null;
    }
}