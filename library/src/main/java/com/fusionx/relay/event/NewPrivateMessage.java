package com.fusionx.relay.event;

public class NewPrivateMessage extends Event {

    public final String nick;

    public NewPrivateMessage(String nick) {
        this.nick = nick;
    }
}