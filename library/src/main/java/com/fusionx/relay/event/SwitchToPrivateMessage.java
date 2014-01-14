package com.fusionx.relay.event;

public class SwitchToPrivateMessage extends Event {

    public final String nick;

    public SwitchToPrivateMessage(String nick) {
        this.nick = nick;
    }
}