package com.fusionx.relay.call;

public class NickChangeCall extends Call {

    public final String newNick;

    public NickChangeCall(String newNick) {
        this.newNick = newNick;
    }
}