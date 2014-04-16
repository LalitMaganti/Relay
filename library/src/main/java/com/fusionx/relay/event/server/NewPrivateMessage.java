package com.fusionx.relay.event.server;

public class NewPrivateMessage extends ServerEvent {

    public final String nick;

    public NewPrivateMessage(final String nick) {
        this.nick = nick;
    }
}